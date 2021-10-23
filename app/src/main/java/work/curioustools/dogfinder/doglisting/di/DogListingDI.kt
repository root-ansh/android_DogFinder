package work.curioustools.dogfinder.doglisting.di
import android.content.Context
import com.google.gson.Gson
import retrofit2.Converter
import work.curioustools.dogfinder.base.NetworkDI
import work.curioustools.dogfinder.doglisting.api.DogsApi
import work.curioustools.dogfinder.doglisting.api.DogsRepo
import work.curioustools.dogfinder.doglisting.api.GetRandomDogUseCase

class DogListingDI {
    companion object {
        fun getDogsApi(context:Context? = null, isDebug:Boolean = false): DogsApi {//todo handle prod/debug
            val client = NetworkDI.getOkHttpClient()
            val gson: Gson = NetworkDI.getGsonOrNull() ?: error("gson is null")
            val convertor: Converter.Factory = NetworkDI.getGsonConvertorOrNull(gson) ?: error("convertor is null")
            val retrofit = NetworkDI.getRetrofit(baseUrl = DogsApi.BASE_URL_WITH_SLASH, client = client, convertorFactories = listOf(convertor))
            return retrofit.create(DogsApi::class.java)
        }

        fun getDogsRepo(api: DogsApi) = DogsRepo(api)

        fun getDogsUseCase(context: Context? = null): GetRandomDogUseCase {
            val api = getDogsApi(context)
            val repo = getDogsRepo(api)
            return GetRandomDogUseCase(repo)
        }
    }
}