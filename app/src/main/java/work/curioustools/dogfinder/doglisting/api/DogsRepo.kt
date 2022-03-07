package work.curioustools.dogfinder.doglisting.api

import work.curioustools.dog_network.*

// a repo class that has the task of making request
class DogsRepo(private val api: DogsApi) {
    suspend fun getRandomDog(): BaseResponse<DogImageDto> {
        return api.getImage().executeAndUnify()
    }
}