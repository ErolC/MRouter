package com.erolc.mrouter.lifecycle

import androidx.compose.runtime.staticCompositionLocalOf
import com.erolc.mrouter.utils.logi


/**
 * @author erolc
 * @since 2024/2/4 16:46
 */
@SinceKotlin("1.0")
class LifecycleDelegate {

 private var call: ((Lifecycle.Event) -> Unit)? = null

 fun onCreate() {
  call?.invoke(Lifecycle.Event.ON_CREATE)
 }

 fun onResume() {
  call?.invoke(Lifecycle.Event.ON_RESUME)
  logi("tag", "foreground")
 }

 fun onPause() {
  call?.invoke(Lifecycle.Event.ON_PAUSE)
  logi("tag", "background")
 }

 fun onDestroy() {
  call?.invoke(Lifecycle.Event.ON_DESTROY)
 }

 internal fun onCall(call: (Lifecycle.Event) -> Unit) {
  this.call = call
 }

}

internal val localLifecycleDelegate = staticCompositionLocalOf { LifecycleDelegate() }
