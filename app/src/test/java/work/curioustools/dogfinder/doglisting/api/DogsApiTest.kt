package work.curioustools.dogfinder.doglisting.api

import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Test
import work.curioustools.dogfinder.BaseTest
import work.curioustools.dogfinder.CallFake
import work.curioustools.dogfinder.base.executeAndUnify
import work.curioustools.dogfinder.base.printBaseResponse
import work.curioustools.dogfinder.doglisting.di.DogListingDI

class DogsApiTest : BaseTest() {

    @Test
    fun testLiveAPi() {
        val data = DogListingDI.getDogsApi(isDebug = true,context = null).getImage().executeAndUnify(enableLogging = true)
        data.printBaseResponse()
        println(data)
    }

    @MockK
    lateinit var dogApiMock:DogsApi

    override fun beforeEachTest() {
        super.beforeEachTest()
    }

    @Test
    fun testFakeAPi(){
        val fakeResp = DogImageDto("http://google.com","success")
        every { dogApiMock.getImage() } returns  CallFake.buildSuccess(fakeResp)


        val request = dogApiMock.getImage()
        val resp = request.execute()

        Assert.assertTrue(resp.body()!=null)
        Assert.assertEquals(resp.body()?.status,fakeResp.status)//fakeResp.status+"/")

    }

}


