package work.curioustools.dogfinder.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding


// utility class for providing viewbinding support to any lifecycle owner
interface VBHolder<B : ViewBinding> {
    var bindingHolder: B?

    fun getNullableBinding() = bindingHolder

    fun withBinding(block: B.() -> Unit) {
        if (bindingHolder == null) {
            error(ERROR_BINDING_IS_NULL)
        }
        else {
            bindingHolder?.apply { block.invoke(this) }
        }
    }

    fun initHolder(binding: B){
        this.bindingHolder = binding
    }

    fun destroyBinding(){
        this.bindingHolder =null
    }


    // A nifty little extension on binding of the children class which will allow
    // to both set and unset at the same time using lifecycle owner.
    fun B.setAsContentView(activity: AppCompatActivity) {
        initHolder(this)
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    owner.lifecycle.removeObserver(this)
                    destroyBinding()
                }
            })
        activity.setContentView(this.root)
    }

    companion object {
        //// View Binding Error Messages ////
        const val ERROR_BINDING_IS_NULL = "Binding Not Found"
    }
}

/*
 * an implementation of VBHolder which will be providing the implementation logic to each activity/fragment
 * since the main implementaion logic could only run after onCreate/ onCreateView, this class only provides
 * us the benefit of not writing `override val bindingHolder: Binding? = null` multiple times
 * */
class VBHolderImpl<VB : ViewBinding> : VBHolder<VB> {
    override var bindingHolder: VB? = null

}