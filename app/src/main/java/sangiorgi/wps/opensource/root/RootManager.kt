package sangiorgi.wps.opensource.root

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

enum class RootState {
    UNKNOWN,
    CHECKING,
    GRANTED,
    DENIED,
    UNAVAILABLE
}

@Singleton
class RootManager @Inject constructor(
    private val rootCapabilityDetector: RootCapabilityDetector
) {
    private val _rootState = MutableStateFlow(RootState.UNKNOWN)
    val rootState: StateFlow<RootState> = _rootState.asStateFlow()

    suspend fun checkRoot() {
        _rootState.value = RootState.CHECKING
        val isAvailable = rootCapabilityDetector.isRootAvailable()
        if (isAvailable) {
            _rootState.value = RootState.GRANTED
        } else {
            // If it's explicitly not available (e.g. denied vs missing binaries)
            _rootState.value = RootState.UNAVAILABLE
        }
    }
}
