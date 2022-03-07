package work.curioustools.dogfinder.doglisting.api


import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test
import work.curioustools.dogfinder.BaseTest
import work.curioustools.dogfinder.CallFake
import work.curioustools.dog_network.*


class DogsRepoTest :BaseTest(){

    @MockK
    lateinit var api: DogsApi

    lateinit var repo: DogsRepo
    override fun beforeEachTest() {
        super.beforeEachTest()
        repo = DogsRepo(api)
    }

    @Test
    fun getRandomDogSuccess() {
        val fakeResp = DogImageDto("http://google.com","success")
        every { api.getImage() } returns  CallFake.buildSuccess(fakeResp)

       runBlocking {
           val request = repo.getRandomDog()
           Assert.assertTrue(request is BaseResponse.Success)
           request as BaseResponse.Success
           Assert.assertEquals(request.body.status,fakeResp.status)//fakeResp.status+"/")
       }
    }


    @Test
    fun getRandomDogFail() {
        every { api.getImage() } returns  CallFake.buildHttpError(BaseResponseType.APP_NULL_RESPONSE_BODY.code,BaseResponseType.APP_NULL_RESPONSE_BODY.msg,BaseResponseType.APP_NULL_RESPONSE_BODY.msg)

        runBlocking {
            val request = repo.getRandomDog()
            Assert.assertTrue(request is BaseResponse.Failure)
            request as BaseResponse.Failure
            Assert.assertEquals(request.exception.message,BaseResponseType.APP_NULL_RESPONSE_BODY.msg)//UNRECOGNISED.msg
        }
    }
}