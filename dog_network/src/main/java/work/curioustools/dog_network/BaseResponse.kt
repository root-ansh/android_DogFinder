package work.curioustools.dog_network

//a helpful base class which helps in unification of various network responses
sealed class BaseResponse<T>(
    open val status: BaseResponseType
) {

    data class Success<T>(val body: T) : BaseResponse<T>(BaseResponseType.SUCCESS)

    data class Failure<T>(
        val body: T? = null,
        override val status: BaseResponseType,
        var exception: Throwable = Exception(status.msg)
    ) : BaseResponse<T>(status)
}