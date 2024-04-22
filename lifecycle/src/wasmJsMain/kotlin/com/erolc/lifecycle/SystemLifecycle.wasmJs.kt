package com.erolc.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.browser.window

@Composable
actual fun SystemLifecycle(call:(Lifecycle.Event)->Unit){
    remember(window){
        window.addEventListener("blur"){
            call(Lifecycle.Event.ON_PAUSE)
        }
        window.addEventListener("focus"){
            call(Lifecycle.Event.ON_RESUME)
        }
    }
}