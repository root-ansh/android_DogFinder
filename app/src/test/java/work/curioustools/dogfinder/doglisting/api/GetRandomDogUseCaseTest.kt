package work.curioustools.dogfinder.doglisting.api

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Test
import work.curioustools.dogfinder.BaseTest
import work.curioustools.dog_network.*
import work.curioustools.dogfinder.getOrAwaitValue

class GetRandomDogUseCaseTest:BaseTest(){

    @MockK
    lateinit var repo: DogsRepo

    lateinit var useCase: GetRandomDogUseCase

    override fun beforeEachTest() {
        super.beforeEachTest()
        useCase = GetRandomDogUseCase(repo)
    }

    @Test
    fun testUseCaseCallingRepoAndReceivingDataCorrectly(){
        val baseResponse = BaseResponse.Success(DogImageDto("http://www.google.com","awesome"))
        coEvery { repo.getRandomDog() }.returns(baseResponse)

        useCase.requestForData(Unit)

        coVerify { repo.getRandomDog() }

        val liveDataValue = useCase.liveData.getOrAwaitValue()
        println("livedataValue = $liveDataValue")
        assert(liveDataValue == baseResponse)
        Assert.assertTrue(liveDataValue is BaseResponse.Success)
        liveDataValue as BaseResponse.Success
        Assert.assertEquals(liveDataValue.body.status , baseResponse.body.status)
    }

}