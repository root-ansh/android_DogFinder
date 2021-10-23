package work.curioustools.dogfinder.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

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

    fun B.setAsContentView(activity: AppCompatActivity) {
        initHolder(this)
        registerLifecycle(activity)
        activity.setContentView(this.root)
    }

    private fun registerLifecycle(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    owner.lifecycle.removeObserver(this)
                    destroyBinding()
                }
            }
        )
    }

    companion object {
        //// View Binding Error Messages ////
        const val ERROR_BINDING_IS_NULL = "Binding Not Found"
    }
}

class VBHolderImpl<VB : ViewBinding> : VBHolder<VB> {
    override var bindingHolder: VB? = null

}