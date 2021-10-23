package work.curioustools.dogfinder.doglisting.api

import work.curioustools.dogfinder.base.BaseResponse
import work.curioustools.dogfinder.base.executeAndUnify

class DogsRepo(private val api: DogsApi) {
    suspend fun getRandomDog(): BaseResponse<DogImageDto> {
        return api.getImage().executeAndUnify()
    }
}