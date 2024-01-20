package com.erolc.mrouter.backstack

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.erolc.mrouter.dialog.DialogOptions
import com.erolc.mrouter.dialog.DialogWrap
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.register.Address
import com.erolc.mrouter.route.PageRouter
import com.erolc.mrouter.scope.DialogScope
import com.erolc.mrouter.scope.WindowScope

class DialogEntry internal constructor(
    private val options: DialogOptions,
    private val entry: StackEntry
) :
    StackEntry(DialogScope(), Address("dialog")) {

    init {
        entry.also {
            it.scope.parentScope = scope
        }
    }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        var animateIn by remember { options.isShowDialog }
        DialogWrap(onDismissRequest = {
            animateIn = false
        }, options) {
            AnimatedVisibility(
                visible = { it }, enter = options.enter, exit = options.exit
            ) {
                Box(
                    Modifier.fillMaxSize().clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            if (options.dismissOnClickOutside)
                                animateIn = false
                        })
                ) {
                    entry.Content(Modifier.align(options.alignment))
                }
                DisposableEffect(Unit) {
                    onDispose {
                        scope.router.backPressedImpl()
                    }
                }
            }
        }
    }

    internal fun dismiss() {
        options.isShowDialog.value = false
    }

    fun buildScope(route: Route, router: PageRouter) {
        scope.router = router
        entry.scope.run {
            argsFlow.value = route.args
            this.router = router
            onResult = route.onResult
            name = route.address
        }
    }

    fun buildWindowScope(windowScope: WindowScope) {
        entry.scope.windowScope = windowScope
    }
}
