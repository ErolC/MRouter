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
import com.erolc.mrouter.utils.loge

class DialogEntry internal constructor(
    private val options: DialogOptions,
    private val entry: PageEntry
) :
    StackEntry(DialogScope(), Address("dialog")) {

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
                        if (!options.isShowDialog.value)
                            entry.scope.router.parentRouter?.backPressedImpl()
                    }
                }
            }
        }
    }

    internal fun dismiss() {
        options.isShowDialog.value = false
    }

    override fun destroy() {
        entry.destroy()
    }
}
