package work.curioustools.dogfinder.base

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.net.InetSocketAddress
import java.net.Socket
import okhttp3.Request
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

// a handy interceptor which will check for both internet connectivity and availability
class InternetCheckInterceptor(private val context: Context? = null):Interceptor{
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

// Provides instances of various objects required for internet connection
object NetworkDI {
    fun getOkHttpClient(
        connectTimeout: Pair<Long, TimeUnit> = Pair(1L, TimeUnit.MINUTES),
        writeTimeout: Pair<Long, TimeUnit> = Pair(1L, TimeUnit.MINUTES),
        readTimeout: Pair<Long, TimeUnit> = Pair(1L, TimeUnit.MINUTES),
        retryOnConnectionFailure: Boolean = true,
        interceptors: List<Interceptor> = listOf(),
        networkInterceptors: List<Interceptor> = listOf(),
        socketFactory: Pair<SSLSocketFactory, X509TrustManager>? = null
    ): OkHttpClient {
        return OkHttpClient.Builder().let { builder ->
            builder.connectTimeout(connectTimeout.first, connectTimeout.second)
            builder.writeTimeout(writeTimeout.first, writeTimeout.second)
            builder.readTimeout(readTimeout.first, readTimeout.second)
            builder.retryOnConnectionFailure(retryOnConnectionFailure)
            interceptors.forEach { builder.addInterceptor(it) }
            networkInterceptors.forEach { builder.addNetworkInterceptor(it) }
            socketFactory?.let { pair -> kotlin.runCatching { builder.sslSocketFactory(pair.first, pair.second) } }
            builder.build()
        }
    }

    fun getGsonOrNull(serializeNulls: Boolean = false): Gson? {
        return kotlin.runCatching {
            GsonBuilder().run {
                if (serializeNulls) serializeNulls()
                create()
            }
        }.getOrNull()
    }

    fun getGsonConvertorOrNull(gson: Gson): GsonConverterFactory? {
        return kotlin.runCatching { GsonConverterFactory.create(gson) }.getOrNull()
    }

    fun getRetrofit(
        baseUrl: String = "",
        client: OkHttpClient,
        convertorFactories: List<Converter.Factory> = listOf(),
        callAdapterFactories: List<CallAdapter.Factory> = listOf(),
        callbackExecutor: Executor? = null,
        callFactory: okhttp3.Call.Factory? = null,
        validateEagerly: Boolean? = null,
    ): Retrofit {
        return Retrofit.Builder().let { builder ->
            convertorFactories.forEach { builder.addConverterFactory(it) }
            callAdapterFactories.forEach { builder.addCallAdapterFactory(it) }
            baseUrl.let { builder.baseUrl(baseUrl) }
            client.let { builder.client(it) }
            callFactory?.let { builder.callFactory(it) }
            callbackExecutor?.let { builder.callbackExecutor(it) }
            validateEagerly?.let { builder.validateEagerly(it) }
            builder.build()
        }
    }

}

/**
 * Retrofit provides a response of format Response(isSuccessful:True/False, body:T/null,...)
 * it treats all failures as null . this Response object on its own is enough to know about the
 * json response, but for convenience we can use a unified sealed class for handling high level
 * distinctions,such as success, failure, token expire failure etc.
 * */
fun <T> Call<T>.executeAndUnify(enableLogging: Boolean = false): BaseResponse<T> {
    return try {
        val response: Response<T?> = this.execute()
        if (enableLogging) {
            this.request().printRequest()
            response.printResponse()
        }
        when {
            response.isSuccessful -> {
                when (val body = response.body()) {
                    null -> BaseResponse.Failure(body, BaseResponseType.APP_NULL_RESPONSE_BODY)
                    else -> BaseResponse.Success(body)
                }
            }
            else -> {
                val code = response.code()
                val body = response.body()
                val status = BaseResponseType.getStatusOrDefault(code)
                val exception = Exception(status.msg)
                val resp = BaseResponse.Failure(body, status, exception)
                resp.exception = exception
                resp
            }
        }
    }
    catch (t: Throwable) {
        BaseResponse.Failure(null, BaseResponseType.getStatusFromException(t), t)
    }

}

// debuggin extensions for logging
fun Request?.printRequest() {

    println("=====<Request>=====")
    this?.let {
        println("logCurrentCall: body :  ${it.body()}")
        println("logCurrentCall: cacheControl ${it.cacheControl()}")
        it.headers().toMultimap().forEach { (key, value) -> println("\t $key : $value") }
        println("logCurrentCall: is https: ${it.isHttps}")
        println("logCurrentCall: method ${it.method()}")
        println("logCurrentCall: url ${it.url()}")
    } ?: println("null")
    println("=====</Request>=====")
}

// debuggin extensions for logging
fun <T> Response<T>?.printResponse() {
    println("=====<Response>=====")
    this?.let {
        println("body = ${it.body()})")
        println("it.code = ${it.code()} ")
        println("it.isSuccessful = ${it.isSuccessful} ")
        println("msg = ${it.message()}")
        println("headers:")
        it.headers().toMultimap().forEach { (key, value) -> println("\t $key : $value") }
        println("it.errorBody = ${it.errorBody()} ")
        println("it.raw request = ${it.raw().request()} ")
        println("it.raw request body= ${it.raw().request().body()} ")
        println("it.raw request headers= ${it.raw().request().headers()} ")
        println("it.raw response= ${it.raw()} ")
        println("it.raw response body= ${it.raw().body()} ")
        println("it.raw response body msg= ${it.raw().message()} ")
    } ?: println("null")
    println("=====</Response>=====")
}

// debuggin extensions for logging
fun <T> BaseResponse<T>.printBaseResponse() {
    println("=====<BaseResponse> ========")
    val resp = this
    println("response status =" + resp.status)
    when (resp) {
        is BaseResponse.Failure -> {
            println("response = ${resp.body}")
            println("exception = ${resp.exception}")
        }
        is BaseResponse.Success -> {
            println("response = ${resp.body}")
        }
    }

    println("=====</BaseResponse> ========")

}


// view extension for loading image via glide
fun AppCompatImageView.loadImageFromInternet(url: String, @DrawableRes placeholder: Int, @DrawableRes error: Int = placeholder, @DrawableRes fallback: Int = placeholder) {
    Glide
        .with(this.context)
        .load(url)
        .placeholder(placeholder)
        .error(error)
        .fallback(fallback)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

// small extension for showing error as a toast
fun <T> BaseResponse.Failure<T>.showAsToast(context: Context) {
    Toast.makeText(context, "${this.status}|| ${this.exception.message}", Toast.LENGTH_SHORT).show()
}