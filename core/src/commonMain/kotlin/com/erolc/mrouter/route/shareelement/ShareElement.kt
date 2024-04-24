package com.erolc.mrouter.route.shareelement

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.erolc.mrouter.model.ShareElement
import com.erolc.mrouter.scope.LocalPageScope
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 共享元素
 * @param name 共享元素的名称
 * @param modifier 共享元素的修饰符
 * @param content 共享元素的界面
 * 需要注意的是，共享元素是需要有大小变换的，那么就需要[content]中的界面调整为[fillMaxSize]，而该元素的具体大小请使用[modifier]进行设置,
 * 也就是共享元素的大小将由[Element]决定，而其内部的[content]只需要沾满[Element]即可
 */
@Composable
fun Element(name: String, modifier: Modifier, content: @Composable Transition<ShareState>.() -> Unit) {
    val scope = LocalPageScope.current
    val position = remember { MutableStateFlow(Rect(Offset.Zero, Size.Zero)) }
    remember(name, scope) {
        val element = ShareElement(name, content, scope.name, position)
        ShareElementController.addElement(element)
    }
    val state by ShareElementController.rememberShareState()
    Box(modifier = modifier.background(Color.Transparent).onGloballyPositioned {
        position.value = it.boundsInRoot()
    }) {
        if (state == Init || state == BeforeShare || state == AfterShare)
            content(updateTransition(Init))
    }
}