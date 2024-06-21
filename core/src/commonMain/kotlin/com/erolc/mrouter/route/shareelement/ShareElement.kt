package com.erolc.mrouter.route.shareelement

import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.lifecycle.Lifecycle
import com.erolc.mrouter.model.ShareElement
import com.erolc.mrouter.scope.LifecycleObserver
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.utils.*
import com.erolc.mrouter.utils.Init
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 共享元素
 * @param key 共享元素的标识
 * @param modifier 共享元素的修饰符
 * @param styles 样式列表，给共享元素内的元素使用
 * @param content 共享元素的界面
 */
@Composable
fun Element(
    key: Any,
    modifier: Modifier = Modifier,
    styles: List<Any> = listOf(),
    content: @Composable ShareTransition.() -> Unit
) {
    val scope = LocalPageScope.current
    val position = remember { MutableStateFlow(Rect(Offset.Zero, Size.Zero)) }
    val element = remember(key, scope) {
        val element = ShareElement(key, content, scope.name, position, styles)
        ShareElementController.addElement(element)
        element
    }
    var lifecycle by rememberInPage("key") {
        mutableStateOf(Lifecycle.Event.ON_RESUME)
    }
    LifecycleObserver { _, event ->
        lifecycle = event
        if (event == Lifecycle.Event.ON_DESTROY) {
            ShareElementController.removeElement(element.tag)
        }
    }
    val state by element.state
    val transition = updateTransition(state)
    val shareTransition = remember(element) {
        ShareTransition(element, element, element, transition)
    }
    ElementContent(modifier, state, shareTransition, onChange = { position.value = it }) {
        content(this)
    }
}

@Composable
private fun ElementContent(
    modifier: Modifier = Modifier,
    state: ShareState,
    shareTransition: ShareTransition,
    onChange: (Rect) -> Unit,
    content: @Composable ShareTransition.() -> Unit
) {
    Box(modifier = modifier.background(Color.Transparent).onGloballyPositioned {
        val bound = it.boundsInRoot()
        if (!bound.isEmpty) onChange(bound)
    }) {
        if (state == Init || state == BeforeStart || state == BeforeEnd) {
            content(shareTransition)
        }
    }
}