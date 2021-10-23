package work.curioustools.dogfinder.base

import work.curioustools.dogfinder.base.BaseResponseType.SUCCESS

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

//a helpful base class which helps in unification of various network responses
sealed class BaseResponse<T>(
    open val status: BaseResponseType
) {

    data class Success<T>(val body: T) : BaseResponse<T>(SUCCESS)

    data class Failure<T>(
        val body: T? = null,
        override val status: BaseResponseType,
        var exception: Throwable = Exception(status.msg)
    ) : BaseResponse<T>(status)
}

//a state enum that tells the type of response based on code received
enum class BaseResponseType(val code: Int, val msg: String) {
    SUCCESS(200, "SUCCESS"),
    NO_INTERNET_CONNECTION(1001, "No Internet found"),
    NO_INTERNET_PACKETS_RECEIVED(1002,"We are unable to connect to our server. Please check with your internet service provider"),
    APP_NULL_RESPONSE_BODY(888, "No Response found"),
    UNRECOGNISED(-1, "unrecognised error in networking");

    override fun toString(): String {
        return "${this.name}(${this.msg} ||code: ${this.code})"
    }

    companion object {
        fun getStatusOrDefault(code: Int? = null): BaseResponseType {
            return values().firstOrNull { it.code == code } ?: UNRECOGNISED
        }

        fun getStatusFromException(t:Throwable):BaseResponseType{
            return values().firstOrNull { it.msg.contentEquals(t.message)} ?: UNRECOGNISED
        }


    }
}


// a concurrency class which is always going to make request in a parallel coroutine
// and update its livedata once the response is available.
// it can also provide the job on which it was started for clubbing together
// with another job or cancelling it
abstract class BaseConcurrencyUseCase<REQUEST, RESP> {
    private val job = Job()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    val liveData = MutableLiveData<RESP>()

    abstract suspend fun getRepoCall(param: REQUEST): RESP

    fun requestForData(param: REQUEST) {
        scope.apply {
            launch(Dispatchers.IO + job) {
                val result: RESP? = getRepoCall(param)
                result?.let { liveData.postValue(it) }
            }
        }
    }

    fun getRawJob() = job

    fun cancel(exception: CancellationException? = null) {
        job.cancel(exception)
    }
}