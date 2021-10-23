package work.curioustools.dogfinder.doglisting.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET

interface DogsApi {
    @GET("api/breeds/image/random")
    fun getImage(): Call<DogImageDto>

    companion object{
        const val BASE_URL_WITH_SLASH = "https://dog.ceo/"
    }
}


data class DogImageDto(
    @SerializedName("message") val url:String? = null,
    @SerializedName("status") val status:String? = null
)
