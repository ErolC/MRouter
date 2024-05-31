package com.erolc.mrouter.route

import androidx.core.bundle.Bundle
import androidx.core.bundle.bundleOf
import com.erolc.mrouter.lifecycle.LifecycleOwnerDelegate

class ResultCallBack(private val lifecycleOwnerDelegate: LifecycleOwnerDelegate) {
    var onResult: RouteResult? = null

    fun onCallResult() {
        val result = lifecycleOwnerDelegate.savedStateHandle.get<Bundle>("result")
        onResult?.invoke(result ?: bundleOf())
    }

    fun setResult(bundle: Bundle) {
        lifecycleOwnerDelegate.savedStateHandle.set("result", bundle)
    }
}