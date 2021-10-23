package work.curioustools.dogfinder.doglisting.api

import com.google.gson.Gson
import org.junit.Test
import retrofit2.Converter
import work.curioustools.dogfinder.base.NetworkUtils
import work.curioustools.dogfinder.base.executeAndUnify
import work.curioustools.dogfinder.base.printBaseResponse

class DogsApiTest{

    @Test
    fun testAPiSuccess() {
        val client = NetworkUtils.GetObjects.getOkHttpClient()
        val gson: Gson = NetworkUtils.GetObjects.getGsonOrNull()?:return
        val convertor: Converter.Factory = NetworkUtils.GetObjects.getGsonConvertorOrNull(gson)?:return
        val retrofit = NetworkUtils.GetObjects.getRetrofit(baseUrl = DogsApi.BASE_URL_WITH_SLASH, client =client, convertorFactories = listOf(convertor))

        val data = retrofit.create(DogsApi::class.java).getImage().executeAndUnify(true)
        data.printBaseResponse()
        print(data)
    }

}