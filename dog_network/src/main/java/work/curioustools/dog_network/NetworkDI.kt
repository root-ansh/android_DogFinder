package work.curioustools.dog_network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

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