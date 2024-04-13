package com.erolc.mrouter.route.shareele

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.erolc.mrouter.utils.loge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 共享元素
 * @param name 共享元素的名称
 * @param modifier 共享元素的修饰符
 * @param content 共享元素的界面
 * 需要注意的是，共享元素是需要有大小变换的，那么就需要[content]中的界面调整为[fillMaxSize]，而该元素的具体大小请使用[modifier]进行设置
 */
@Composable
fun Element(name: String, modifier: Modifier, content: @Composable () -> Unit) {
    val controller = LocalShareEleController.current
    val scope = LocalPageScope.current
    val position = remember { MutableStateFlow(Rect(Offset.Zero, Size.Zero)) }
    remember(name, scope) {
        val element = ShareElement(name, content, scope.name, position)
        loge("tag", "add_ele")
        controller.elements.add(element)
    }
    val state by controller.shareState.asStateFlow().collectAsState()
    Box(modifier = modifier.background(Color.Transparent).onGloballyPositioned {
        position.value = it.boundsInRoot()
    }) {
        if (state == Init || state == BeforeShare || state == AfterShare)
            content()
    }
}