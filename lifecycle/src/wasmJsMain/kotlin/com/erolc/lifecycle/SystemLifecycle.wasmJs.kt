package com.erolc.lifecycle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener

@Composable
actual fun SystemLifecycle(call: (Lifecycle.Event) -> Unit) {
    DisposableEffect(window) {
        val blurListener = object : EventListener {
            override fun handleEvent(event: Event) {
                call(Lifecycle.Event.ON_PAUSE)
            }
        }
        val focusListener = object : EventListener {
            override fun handleEvent(event: Event) {
                call(Lifecycle.Event.ON_RESUME)
            }
        }
        window.addEventListener("blur", blurListener)
        window.addEventListener("focus", focusListener)
        onDispose {
            window.removeEventListener("blur", blurListener)
            window.removeEventListener("focus", focusListener)
        }
    }
}