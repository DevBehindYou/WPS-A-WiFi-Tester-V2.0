package sangiorgi.wps.opensource.ui.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import sangiorgi.wps.opensource.core.logging.AppLogger
import sangiorgi.wps.opensource.data.AppSettings
import sangiorgi.wps.opensource.data.SettingsRepository
import sangiorgi.wps.opensource.root.RootManager
import sangiorgi.wps.opensource.root.RootState
import sangiorgi.wps.opensource.root.SuCommandExecutor
import sangiorgi.wps.opensource.wifi.WifiInterface
import sangiorgi.wps.opensource.wifi.WifiInterfaceDetector
import sangiorgi.wps.opensource.wifi.WifiNetwork
import sangiorgi.wps.opensource.wifi.WifiScanner
import sangiorgi.wps.opensource.wps.SupplicantAdapter
import sangiorgi.wps.opensource.wps.WpsPinManager
import sangiorgi.wps.opensource.wps.WpsState

@OptIn(ExperimentalCoroutinesApi::class)
class ScannerViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var rootManager: RootManager
    private lateinit var wifiScanner: WifiScanner
    private lateinit var wifiInterfaceDetector: WifiInterfaceDetector
    private lateinit var supplicantAdapter: SupplicantAdapter
    private lateinit var pinManager: WpsPinManager
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var logger: AppLogger
    private lateinit var viewModel: ScannerViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        rootManager = mock(RootManager::class.java)
        wifiScanner = mock(WifiScanner::class.java)
        wifiInterfaceDetector = mock(WifiInterfaceDetector::class.java)
        supplicantAdapter = mock(SupplicantAdapter::class.java)
        val executor = mock(SuCommandExecutor::class.java)
        logger = AppLogger()
        pinManager = WpsPinManager(executor, logger)
        settingsRepository = mock(SettingsRepository::class.java)

        `when`(rootManager.rootState).thenReturn(MutableStateFlow(RootState.GRANTED))
        `when`(wifiScanner.isScanning).thenReturn(MutableStateFlow(false))
        `when`(wifiScanner.isWifiEnabled()).thenReturn(true)
        `when`(wifiScanner.isLocationEnabled()).thenReturn(true)
        `when`(settingsRepository.settings).thenReturn(MutableStateFlow(AppSettings()))

        viewModel = ScannerViewModel(
            rootManager = rootManager,
            wifiScanner = wifiScanner,
            wifiInterfaceDetector = wifiInterfaceDetector,
            supplicantAdapter = supplicantAdapter,
            pinManager = pinManager,
            settingsRepository = settingsRepository,
            logger = logger
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testInitialState() {
        val state = viewModel.uiState.value
        assertNotNull(state)
        assertEquals(WpsState.IDLE, state.wpsState)
        assertFalse(state.isScanning)
        assertNull(state.targetNetwork)
    }

    @Test
    fun testSelectNetworkForPbc() = runTest(testDispatcher) {
        val network = WifiNetwork(
            ssid = "TestRouter",
            bssid = "00:11:22:33:44:55",
            capabilities = "[WPS]",
            rssi = -50,
            frequency = 2412,
            isWpsAvailable = true
        )

        `when`(supplicantAdapter.startPbc("wlan0", "00:11:22:33:44:55"))
            .thenReturn(flowOf(WpsState.WAITING_FOR_ROUTER, WpsState.CONNECTED))

        viewModel.selectNetworkForPbc(network)

        val state = viewModel.uiState.value
        assertEquals(network, state.targetNetwork)
        assertTrue(state.showWpsProgressDialog)
    }

    @Test
    fun testPinValidation() {
        assertTrue(viewModel.validatePin("12345670"))
        assertTrue(viewModel.validatePin("1234"))
        assertFalse(viewModel.validatePin("12345"))
        assertFalse(viewModel.validatePin("12345678"))
    }
}
