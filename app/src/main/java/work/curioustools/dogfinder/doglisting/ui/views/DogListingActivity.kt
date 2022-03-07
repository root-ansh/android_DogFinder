package work.curioustools.dogfinder.doglisting.ui.views

import android.os.Bundle
import android.view.View
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import work.curioustools.dogfinder.R
import work.curioustools.dogfinder.base.*
import work.curioustools.dogfinder.databinding.ActivityDogListingBinding
import work.curioustools.dogfinder.doglisting.di.DogListingDI
import work.curioustools.dogfinder.doglisting.ui.models.DogImage
import work.curioustools.dogfinder.doglisting.ui.viewmodels.DogsViewModel
import work.curioustools.dog_network.*

class DogListingActivity : BaseActivity(), VBHolder<ActivityDogListingBinding> by VBHolderImpl() {

    private val viewModel by lazy { DogsViewModel.getInstance(this, DogListingDI.getDogsUseCase(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityDogListingBinding.inflate(layoutInflater).setAsContentView(this)

        initObservers()

        withBinding {
            btPrevious.setOnClickListener { requestOrSetCachedData(isForwardRequest = false) }
            btNext.setOnClickListener { requestOrSetCachedData(isForwardRequest = true) }
            //initialCall
            btNext.performClick()
        }
    }

    private fun requestOrSetCachedData(isForwardRequest: Boolean) {
        val cache = viewModel.fetchRandomDogOrGetCachedDog(isForwardRequest)
        if (cache == null) setDynamicData(null, BadDataBehavior.SHOW_LOADING)
        else setDynamicData(cache, BadDataBehavior.SHOW_FALLBACK)
    }

    private fun initObservers() {
        val ctx = this
        viewModel.randomDogLiveData.observe(ctx) {
            when (it) {
                is BaseResponse.Success -> {
                    val data = it.body
                    setDynamicData(data, BadDataBehavior.SHOW_FALLBACK)
                }
                is BaseResponse.Failure -> {
                    it.showAsToast(ctx)
                    setDynamicData(null, BadDataBehavior.SHOW_FALLBACK)
                }
            }
        }
    }

    private fun setDynamicData(data: DogImage?, badDataBehavior: BadDataBehavior) {
        getNullableBinding()?.run {
            val ctx = root.context ?: return

            //checking if data is bad
            if (data == null || data.image.isBlank()) {
                when (badDataBehavior) {
                    BadDataBehavior.SHOW_LOADING -> {
                        ivDog.setImageResource(R.drawable.bg_image_placeholder)
                        ivLoading.visibility = View.VISIBLE
                    }
                    BadDataBehavior.SHOW_FALLBACK -> {
                        ivDog.setImageResource(R.drawable.bg_image_fallback)
                        ivLoading.visibility = View.GONE
                    }
                }
            }
            else {
                ivLoading.visibility = View.GONE
                btPrevious.run {
                    isEnabled = data.requestCount > 1
                    val bgTint = if (data.requestCount > 1) R.color.blue_1a237e else R.color.grey_8f8f8f
                    backgroundTintList = ContextCompat.getColorStateList(ctx, bgTint)
                }
                GlideUtil.loadImageFromInternet(data.image,ivDog)
//                ivDog.loadImageFromInternet(
//                    url = data.image,
//                    placeholder = R.drawable.bg_image_placeholder,
//                    error = R.drawable.bg_image_error,
//                    fallback = R.drawable.bg_image_fallback,
//                )
            }
        }
    }

    @Keep
    enum class BadDataBehavior { SHOW_LOADING, SHOW_FALLBACK }
}