package work.curioustools.dogfinder.doglisting.di
import android.content.Context
import com.google.gson.Gson
import okhttp3.Interceptor
import retrofit2.Converter
import work.curioustools.dog_network.*
import work.curioustools.dogfinder.doglisting.api.DogsApi
import work.curioustools.dogfinder.doglisting.api.DogsRepo
import work.curioustools.dogfinder.doglisting.api.GetRandomDogUseCase

// a class providing various objects used in this module
class DogListingDI {
    companion object {
        fun getDogsApi(context:Context? = null, isDebug:Boolean = false): DogsApi {//todo handle prod/debug
            val interceptors = mutableListOf<Interceptor>()
            if(context!=null) interceptors.add(InternetCheckInterceptor(context))
            val client = NetworkDI.getOkHttpClient(interceptors = interceptors)
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