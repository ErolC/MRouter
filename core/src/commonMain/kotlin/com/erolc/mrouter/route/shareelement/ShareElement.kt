package com.erolc.mrouter.route.shareelement

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
 * @param content 共享元素的界面
 *
 */
@Composable
fun Element(key: String, modifier: Modifier, content: @Composable Transition<ShareState>.() -> Unit) {
    val scope = LocalPageScope.current
    val position = remember { MutableStateFlow(Rect(Offset.Zero, Size.Zero)) }
    val element = remember(key, scope) {
        val element = ShareElement(key, content, scope.name, position)
        ShareElementController.addElement(element)
        element
    }
    LifecycleObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            ShareElementController.removeElement(element.tag)
        }
    }
    val state by element.state
    Box(modifier = modifier.background(Color.Transparent).onGloballyPositioned {
        val bound = it.boundsInRoot()
        if (!bound.isEmpty) position.value = bound
    }) {
        if (state == Init || state == BeforeStart || state == BeforeEnd) content(updateTransition(state))
    }
}