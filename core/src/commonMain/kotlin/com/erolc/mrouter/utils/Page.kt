package com.erolc.mrouter.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.platform.isMobile
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.route.router.PageRouter
import com.erolc.mrouter.route.shareelement.ShareElementController
import com.erolc.mrouter.route.transform.ResumeState
import com.erolc.mrouter.scope.HostScope
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import com.erolc.mrouter.scope.WindowScope
import com.erolc.mrouter.window.HostSize
import kotlinx.coroutines.CoroutineScope

/**
 * 用于定义一个composable为页面
 * ```kotlin
 * @Composable
 * fun TestPage() = Page{
 * // code...
 * }
 * ```
 */
@Composable
fun Page(
    modifier: Modifier = Modifier.background(Color.White).safeContentPadding(),
    block: @Composable PageScope.() -> Unit
) {
    val fManager = LocalFocusManager.current
    val scope = LocalPageScope.current
    Box(if (isMobile) modifier.pointerInput(scope) { detectTapGestures { fManager.clearFocus(false) } } else modifier
    ) {
        block(scope)
    }
}

suspend fun LifecycleOwner.repeatOnLifecycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() -> Unit
): Unit = lifecycle.repeatOnLifecycle(state, block)


@Composable
internal fun PageRouter.HostContent(
    modifier: Modifier,
    hostScope: HostScope,
    lifecycleOwner: LifecycleOwner,
    block: @Composable () -> Unit = {}
) {
    DisposableEffect(lifecycleOwner) {
        // Setup the pageRouter with proper owners
        setLifecycleOwner(lifecycleOwner)
        onDispose { }
    }
    val density = LocalDensity.current
    Box(modifier.fillMaxSize().onGloballyPositioned {
        hostScope.run {
            size.value = it.boundsInRoot().size
            setHostSize(HostSize.calculateFromSize(with(density) { size.value.toDpSize() }))
        }
    }) {
        val stack by getPlayStack()
            .collectAsState(getBackStack().value.map { it as PageEntry })

        if (stack.size == 1) {
            stack.first().run {
                transformState.value = ResumeState
                shareTransform(null)
            }
        } else
            stack.last().shareTransform(stack.first())

        stack.forEach { it.Content(Modifier) }

        if (stack.size == 2)
            ShareElementController.initShare(stack.first(), stack.last())

        block()
    }

}