package work.curioustools.dogfinder.doglisting.api

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import work.curioustools.dogfinder.base.executeAndUnify
import work.curioustools.dogfinder.base.printBaseResponse
import work.curioustools.dogfinder.doglisting.di.DogListingDI

class DogsApiTest{
    @ExperimentalCoroutinesApi
    @Test
    fun testAPiSuccess() {
        val data = DogListingDI.getDogsApi(isDebug = true).getImage().executeAndUnify(enableLogging = true)
        data.printBaseResponse()
        println(data)
    }

}