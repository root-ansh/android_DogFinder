package work.curioustools.dog_network

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import okhttp3.Interceptor
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

// a handy interceptor which will check for both internet connectivity and availability
class InternetCheckInterceptor(private val context: Context? = null): Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        if (context == null) return chain.proceed(chain.request())

        return when {
            !isConnectedToInternetProvider(context) -> throw Exception(BaseResponseType.NO_INTERNET_CONNECTION.msg)
            !isReceivingInternetPackets() -> throw IOException(BaseResponseType.NO_INTERNET_PACKETS_RECEIVED.msg)
            else -> chain.proceed(chain.request())
        }
    }

    companion object {
        @JvmStatic
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        fun isConnectedToInternetProvider(ctx: Context): Boolean {
            val cm = ctx.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            cm ?: return false
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {

                    val currentNetwork = cm.activeNetwork ?: return false
                    val capabilities = cm.getNetworkCapabilities(currentNetwork) ?: return false
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
                else -> {
                    val currentNetworkINFO = cm.activeNetworkInfo ?: return false
                    currentNetworkINFO.isConnectedOrConnecting
                            || currentNetworkINFO.type == ConnectivityManager.TYPE_WIFI
                            || currentNetworkINFO.type == ConnectivityManager.TYPE_MOBILE
                }
            }
        }

        @JvmStatic
        @WorkerThread
        fun isReceivingInternetPackets(): Boolean {
            val dnsPort = 53
            val googleIp = "8.8.8.8"
            val timeOut = 1500
            return kotlin.runCatching {
                val socket = Socket()
                val inetAddress = InetSocketAddress(googleIp, dnsPort)
                socket.connect(inetAddress, timeOut)
                socket.close()
                true
            }.getOrDefault(false)
        }

    }
}