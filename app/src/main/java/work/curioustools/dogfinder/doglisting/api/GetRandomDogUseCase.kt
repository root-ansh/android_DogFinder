package work.curioustools.dogfinder.doglisting.api

import work.curioustools.dogfinder.base.BaseConcurrencyUseCase
import work.curioustools.dogfinder.base.BaseResponse

// a usecase class which mainly provides the support of concurrent requests. viewmodel makes an api call
// through this in main thread, but the data is receieved in a parallel thread and transmitted
// via this usecase's livedata
class GetRandomDogUseCase(private val repo: DogsRepo) : BaseConcurrencyUseCase<Unit, BaseResponse<DogImageDto>>() {
    override suspend fun getRepoCall(param: Unit): BaseResponse<DogImageDto> {
        return repo.getRandomDog()
    }
}