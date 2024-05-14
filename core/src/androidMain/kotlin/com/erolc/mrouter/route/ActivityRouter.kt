package com.erolc.mrouter.route

import android.content.Context
import android.os.Bundle
import androidx.core.os.bundleOf

/**
 * Activity的路由器，和[ActivityRouterDispatcher]配合可实现从compose跳转到Activity并获取activity的返回值
 */
class ActivityRouter<I, O> {
    internal var input: I? = null
    internal var onResult: ((O) -> Unit)? = null

    /**
     * 从compose中传递给activity的参数
     */
    var args: Bundle = bundleOf()
        internal set

    internal val result: Bundle = bundleOf()

    var context: Context? = null
        internal set


    operator fun invoke(input: I, block: (O) -> Unit = {}) {
        this.input = input
        onResult = block
    }

    /**
     * 设置activity的返回值
     */
    fun setResult(block: Bundle.() -> Unit) {
        result.run(block)
    }

}