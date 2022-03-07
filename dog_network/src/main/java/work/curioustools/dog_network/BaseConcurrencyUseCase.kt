package work.curioustools.dog_network


import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*


// a concurrency class which is always going to make request in a parallel coroutine
// and update its livedata once the response is available.
// it can also provide the job on which it was started for clubbing together
// with another job or cancelling it
abstract class BaseConcurrencyUseCase<REQUEST, RESP> {
    private val job = Job()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    val liveData = MutableLiveData<RESP>()

    abstract suspend fun getRepoCall(param: REQUEST): RESP

    fun requestForData(param: REQUEST) {
        scope.apply {
            launch(Dispatchers.IO + job) {
                val result: RESP? = getRepoCall(param)
                result?.let { liveData.postValue(it) }
            }
        }
    }

    fun getRawJob() = job

    fun cancel(exception: CancellationException? = null) {
        job.cancel(exception)
    }
}