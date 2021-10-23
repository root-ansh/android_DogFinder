package work.curioustools.dogfinder.doglisting.ui.viewmodels

import androidx.lifecycle.*
import work.curioustools.dogfinder.base.BaseResponse
import work.curioustools.dogfinder.doglisting.api.DogImageDto
import work.curioustools.dogfinder.doglisting.api.GetRandomDogUseCase
import work.curioustools.dogfinder.doglisting.ui.models.DogImage

class DogsViewModel(
    private val getRandomDogUseCase: GetRandomDogUseCase
) : ViewModel() {

    private val cachedRequests = mutableMapOf<Int, DogImage>()

    val randomDogLiveData: LiveData<BaseResponse<DogImage>> = Transformations.switchMap(getRandomDogUseCase.liveData) {
        val livedata = MutableLiveData<BaseResponse<DogImage>>()
        when (it) {
            is BaseResponse.Success -> {
                val data = DogImage(currentReqCount, it.body.url ?: "")
                cachedRequests[currentReqCount] = data
                livedata.value = BaseResponse.Success(data)
            }
            is BaseResponse.Failure -> {
                livedata.value = BaseResponse.Failure(null, it.statusCode, it.exception)
            }
        }
        livedata
    }

    private var currentReqCount = 0

    fun fetchRandomDogOrGetCachedDog(isForwardRequest: Boolean): DogImage? {
       currentReqCount = if (isForwardRequest) currentReqCount + 1
        else {
            if (currentReqCount > 0) currentReqCount - 1
            else 0
        }
        return if(cachedRequests[currentReqCount]!=null) cachedRequests[currentReqCount]
        else{
            getRandomDogUseCase.requestForData(Unit)
            null
        }
    }


    companion object {
        fun getInstance(owner: ViewModelStoreOwner, getRandomDogUseCase: GetRandomDogUseCase): DogsViewModel {
            val f = object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return DogsViewModel(getRandomDogUseCase) as T
                }
            }
            return ViewModelProvider(owner, f).get(DogsViewModel::class.java)
        }

    }
}