package work.curioustools.dogfinder.doglisting.api

import retrofit2.Call
import retrofit2.http.GET

interface DogsApi {
    @GET("api/breeds/image/random")
    fun getImage(): Call<DogImageDto>

    companion object{
        const val BASE_URL_WITH_SLASH = "https://dog.ceo/"
    }
}