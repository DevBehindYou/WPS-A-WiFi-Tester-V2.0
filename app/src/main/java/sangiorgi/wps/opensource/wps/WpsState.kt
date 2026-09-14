package sangiorgi.wps.opensource.wps

enum class WpsState {
    IDLE,
    PREPARING,
    WAITING_FOR_ROUTER,
    AUTHENTICATING,
    ASSOCIATING,
    OBTAINING_IP,
    CONNECTED,
    FAILED,
    TIMED_OUT,
    CANCELLED,
    UNSUPPORTED
}
