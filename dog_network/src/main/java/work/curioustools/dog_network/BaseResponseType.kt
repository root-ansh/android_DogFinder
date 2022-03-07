package work.curioustools.dog_network

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

        fun getStatusFromException(t:Throwable): BaseResponseType {
            return values().firstOrNull { it.msg.contentEquals(t.message)} ?: UNRECOGNISED
        }


    }
}