package xyz.dean.androiddemos.demos.vpntest

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.net.NetworkInterface
import java.net.SocketException


object NetworkUtils {
    private const val TAG = "NetworkUtils"

    fun isVpnConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cm.activeNetwork
            if (network != null) {
                val capabilities = cm.getNetworkCapabilities(network)
                if (capabilities != null) {
                    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                }
            }
        } else {
            val networkInfo = cm.activeNetworkInfo
            if (networkInfo != null) {
                return networkInfo.type == ConnectivityManager.TYPE_VPN
            }
        }
        return false
    }

    fun isVpnConnected(): Boolean {
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                if (networkInterface.isUp() && !networkInterface.isLoopback) {
                    if (networkInterface.isVirtual)
                        return true
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }

        val dnsServers = System.getProperty("net.dns1")
        return dnsServers != null && dnsServers.contains("10.0.0.0")
    }
}