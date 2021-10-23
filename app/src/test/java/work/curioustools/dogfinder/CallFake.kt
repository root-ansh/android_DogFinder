package work.curioustools.dogfinder

import okhttp3.MediaType
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CallFake<T>(
    private val response: Response<T>
)
    : Call<T> {

    companion object {
        inline fun <reified T> buildSuccess(body: T): CallFake<T> {
            return CallFake(Response.success(body))
        }

        inline fun <reified T> buildHttpError(errorCode: Int, contentType: String, content: String): CallFake<T> {
            return CallFake(Response.error(errorCode, ResponseBody.create(MediaType.parse(contentType), content)))
        }
    }

    override fun execute(): Response<T> = response

    override fun enqueue(callback: Callback<T>?) {}

    override fun isExecuted(): Boolean = false

    override fun clone(): Call<T> = this

    override fun isCanceled(): Boolean = false

    override fun cancel() {}

    override fun request(): Request? = null
    override fun timeout(): Timeout = Timeout.NONE
}