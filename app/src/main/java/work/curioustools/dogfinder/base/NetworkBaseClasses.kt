package work.curioustools.dogfinder.base

import com.google.gson.annotations.SerializedName
import work.curioustools.dogfinder.base.BaseResponseType.SUCCESS


import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*

open class BaseDto(
    @SerializedName("error") open var error: String? = null,
    @SerializedName("page") open val currentPage: Int? = null,
    @SerializedName("total_pages") open val totalPages: Int? = null,
    @SerializedName("per_page") open val perPageEntries: Int? = null,
    @SerializedName("total") open val totalEntries: Int? = null,
    @SerializedName("limit") val limit: Int? = null,
    @SerializedName("offset") val offset: Int? = null,
    @SerializedName("success") val success: Boolean? = null
)

// base class which helps in unification of various network responses
sealed class BaseResponse<T>(
    open val statusCode: Int,
    open var statusMsg: String
) {

    data class Success<T>(val body: T) : BaseResponse<T>(SUCCESS.code, SUCCESS.msg)

    data class Failure<T>(
        val body: T? = null,
        override val statusCode: Int,
        var exception: Throwable = Exception(BaseResponseType.getStatusMsgOrDefault(statusCode))
    ) : BaseResponse<T>(statusCode, exception.message ?: "")
}

enum class BaseResponseType(val code: Int, val msg: String) {
    SUCCESS(200, "SUCCESS"),
    NO_INTERNET_CONNECTION(1001, "No Internet found"),
    USER_NOT_FOUND(400, "User Not Found"),
    APP_NULL_RESPONSE_BODY(888, "No Response found"),
    SERVER_FAILURE(500, "server failure"),
    SERVER_DOWN_502(502, "server down 502"),
    SERVER_DOWN_503(503, "server down 503"),
    SERVER_DOWN_504(504, "server down 504"),
    UNRECOGNISED(-1, "unrecognised error in networking");

    companion object {
        fun getStatusMsgOrDefault(code: Int): String {
            return getStatusMsgOrNull(code) ?: UNRECOGNISED.msg
        }

        fun getStatusMsgOrNull(code: Int): String? {
            val enumVal = values().firstOrNull { it.code == code }
            return enumVal?.msg
        }
    }
}

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

    fun cancel(exception: CancellationException? = null) {
        job.cancel(exception)
    }
}