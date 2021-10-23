package work.curioustools.dogfinder.doglisting.api

import work.curioustools.dogfinder.base.BaseConcurrencyUseCase
import work.curioustools.dogfinder.base.BaseResponse

class GetRandomDogUseCase(private val repo: DogsRepo) : BaseConcurrencyUseCase<Unit, BaseResponse<DogImageDto>>() {
    override suspend fun getRepoCall(param: Unit): BaseResponse<DogImageDto> {
        return repo.getRandomDog()
    }
}