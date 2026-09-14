package sangiorgi.wps.opensource.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sangiorgi.wps.opensource.core.logging.AppLogger
import sangiorgi.wps.opensource.core.logging.LogLevel
import sangiorgi.wps.opensource.data.SettingsRepository
import sangiorgi.wps.opensource.root.RootManager
import sangiorgi.wps.opensource.root.RootState
import sangiorgi.wps.opensource.wifi.WifiInterfaceDetector
import sangiorgi.wps.opensource.wifi.WifiNetwork
import sangiorgi.wps.opensource.wifi.WifiScanner
import sangiorgi.wps.opensource.wps.SupplicantAdapter
import sangiorgi.wps.opensource.wps.WpsPinManager
import sangiorgi.wps.opensource.wps.WpsState
import javax.inject.Inject

data class ScannerUiState(
    val isScanning: Boolean = false,
    val networks: List<WifiNetwork> = emptyList(),
    val rootStatus: RootState = RootState.CHECKING,
    val activeInterface: String? = null,
    val isWifiEnabled: Boolean = true,
    val isLocationEnabled: Boolean = true,
    val wpsState: WpsState = WpsState.IDLE,
    val targetNetwork: WifiNetwork? = null,
    val selectedNetworkForSheet: WifiNetwork? = null,
    val showPinDialog: Boolean = false,
    val showWpsProgressDialog: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    val rootManager: RootManager,
    private val wifiScanner: WifiScanner,
    private val wifiInterfaceDetector: WifiInterfaceDetector,
    private val supplicantAdapter: SupplicantAdapter,
    private val pinManager: WpsPinManager,
    private val settingsRepository: SettingsRepository,
    private val logger: AppLogger
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            rootManager.rootState.collect { rState ->
                _uiState.update { it.copy(rootStatus = rState) }
                if (rState == RootState.GRANTED) {
                    refreshInterface()
                }
            }
        }
        viewModelScope.launch {
            wifiScanner.isScanning.collect { scanning ->
                _uiState.update { it.copy(isScanning = scanning) }
            }
        }
        refreshSystemState()
    }

    fun refreshSystemState() {
        viewModelScope.launch {
            rootManager.checkRoot()
            refreshInterface()
            val wifiOn = wifiScanner.isWifiEnabled()
            val locOn = wifiScanner.isLocationEnabled()
            _uiState.update { it.copy(isWifiEnabled = wifiOn, isLocationEnabled = locOn) }
        }
    }

    private suspend fun refreshInterface() {
        val settings = settingsRepository.settings.value
        val iface = if (settings.autoDetectInterface) {
            wifiInterfaceDetector.detectActiveInterface()?.name ?: "wlan0"
        } else {
            settings.manualInterface
        }
        _uiState.update { it.copy(activeInterface = iface) }
    }

    fun scanNetworks() {
        viewModelScope.launch {
            logger.log("ScannerViewModel", "scan", "User triggered Wi-Fi scan", level = LogLevel.INFO)
            _uiState.update {
                it.copy(
                    isWifiEnabled = wifiScanner.isWifiEnabled(),
                    isLocationEnabled = wifiScanner.isLocationEnabled(),
                    errorMessage = null
                )
            }
            wifiScanner.startScan()
            val results = wifiScanner.getNearbyNetworks()
            _uiState.update { it.copy(networks = results) }
            logger.log("ScannerViewModel", "scan", "Loaded ${results.size} networks into UI", level = LogLevel.INFO)
        }
    }

    fun selectNetworkForPbc(network: WifiNetwork) {
        val iface = _uiState.value.activeInterface ?: "wlan0"
        _uiState.update {
            it.copy(
                targetNetwork = network,
                showWpsProgressDialog = true,
                wpsState = WpsState.PREPARING
            )
        }
        viewModelScope.launch {
            logger.log("ScannerViewModel", "wps_pbc", "Starting WPS PBC on $iface to ${network.ssid} (${network.bssid})", level = LogLevel.INFO)
            supplicantAdapter.startPbc(iface, network.bssid).collect { state ->
                _uiState.update { it.copy(wpsState = state) }
            }
        }
    }

    fun openMethodSheet(network: WifiNetwork) {
        _uiState.update { it.copy(selectedNetworkForSheet = network) }
    }

    fun dismissMethodSheet() {
        _uiState.update { it.copy(selectedNetworkForSheet = null) }
    }

    fun openPinDialog(network: WifiNetwork) {
        _uiState.update {
            it.copy(
                targetNetwork = network,
                showPinDialog = true,
                errorMessage = null
            )
        }
    }

    fun dismissPinDialog() {
        _uiState.update { it.copy(showPinDialog = false) }
    }

    fun confirmPinConnection(pin: String) {
        val network = _uiState.value.targetNetwork ?: return
        if (!pinManager.isValidPin(pin)) {
            _uiState.update { it.copy(errorMessage = "Invalid PIN: Enter a valid 4-digit or 8-digit WPS PIN with correct checksum.") }
            return
        }

        val iface = _uiState.value.activeInterface ?: "wlan0"
        _uiState.update {
            it.copy(
                showPinDialog = false,
                showWpsProgressDialog = true,
                wpsState = WpsState.PREPARING,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            logger.log("ScannerViewModel", "wps_pin", "Starting WPS PIN on $iface to ${network.ssid} (${network.bssid})", level = LogLevel.INFO)
            supplicantAdapter.startPin(iface, network.bssid, pin).collect { state ->
                _uiState.update { it.copy(wpsState = state) }
            }
        }
    }

    fun cancelActiveWps() {
        val iface = _uiState.value.activeInterface ?: "wlan0"
        viewModelScope.launch {
            logger.log("ScannerViewModel", "wps_cancel", "Cancelling active WPS session on $iface", level = LogLevel.INFO)
            supplicantAdapter.cancel(iface)
            _uiState.update { it.copy(wpsState = WpsState.CANCELLED) }
        }
    }

    fun dismissWpsProgressDialog() {
        _uiState.update {
            it.copy(
                showWpsProgressDialog = false,
                targetNetwork = null,
                wpsState = WpsState.IDLE
            )
        }
    }

    fun validatePin(pin: String): Boolean {
        return pinManager.isValidPin(pin)
    }

    fun isChecksumValid(pin: String): Boolean {
        return pinManager.validateChecksum(pin)
    }
}
