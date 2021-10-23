package work.curioustools.dogfinder.doglisting.api

import work.curioustools.dogfinder.base.BaseResponse
import work.curioustools.dogfinder.base.executeAndUnify

// a repo class that has the task of making request
class DogsRepo(private val api: DogsApi) {
    suspend fun getRandomDog(): BaseResponse<DogImageDto> {
        return api.getImage().executeAndUnify()
    }
}