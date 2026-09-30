package ir.armaneleven.v2eleven.core

/**
 * Common interface for proxy engines.
 * Allows swapping between Xray and Sing-Box implementations.
 */
interface ProxyEngine {
    val name: String
    val isRunning: Boolean
    fun start(configJson: String, service: android.net.VpnService): Boolean
    fun stop()
}
