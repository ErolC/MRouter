package com.erolc.mrouter.dialog

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.utils.PlatformDialog
import com.erolc.mrouter.utils.loge

private fun Modifier.usePlatformDefaultWidth(usePlatformDefaultWidth: Boolean): Modifier {
    val padding = if (usePlatformDefaultWidth) 20.dp else 0.dp
    return padding(padding, 0.dp, padding, 0.dp)
}

/**
 * 给dialog上一层包装，自定义弹框外的背景并加上连贯动画
 */
@Composable
fun DialogWrap(
    onDismissRequest: () -> Unit,
    options: DialogOptions, content: @Composable Transition<Boolean>.() -> Unit
) {
    var currentState by remember {
        options.isShowDialog
    }
    //todo 由于加上系统的dialog会存在切换的一些问题，所以这里先不使用
//    PlatformDialog(onDismissRequest = {
//        currentState = false
//    }, options) {
    LaunchedEffect(Unit) {
        currentState = true
    }
    val transition = updateTransition(targetState = currentState, label = "dialogTransition")
    val alpha by transition.animateFloat(label = "alpha") {
        if (it) 1f else 0f
    }
    Box(
        Modifier.fillMaxSize()
    ) {
        //dialog 外部背景
        Box(
            Modifier
                .fillMaxSize()
                .alpha(alpha)
                .background(options.scrimColor)
                .usePlatformDefaultWidth(options.usePlatformDefaultWidth)
                .clickable(indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {
                        if (options.dismissOnClickOutside) onDismissRequest()
                    })
        )
        content(transition)
    }
//    }
}