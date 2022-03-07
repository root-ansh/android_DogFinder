package work.curioustools.dog_network

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import okhttp3.Request
import retrofit2.*

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




// small extension for showing error as a toast
fun <T> BaseResponse.Failure<T>.showAsToast(context: Context) {
    Toast.makeText(context, "${this.status}|| ${this.exception.message}", Toast.LENGTH_SHORT).show()
}