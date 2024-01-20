package com.erolc.mrouter.dialog

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

private const val DefaultScrimOpacity = 0.6f
private val DefaultScrimColor = Color.Black.copy(alpha = DefaultScrimOpacity)

class DialogBuilder {

    var alignment: Alignment = Alignment.Center

    var dismissOnBackPress: Boolean = true
    var dismissOnClickOutside: Boolean = true
    var usePlatformDefaultWidth: Boolean = true

    // 请注意，该属性对android无用
    var usePlatformInsets: Boolean = true

    var scrimColor: Color = DefaultScrimColor

    var enter: EnterTransition = fadeIn()

    var exit: ExitTransition = fadeOut()

    //注意，以下两个属性只对android有效
    var decorFitsSystemWindows: Boolean = true

    //true = 开，false-关，null，其对应着androidx.compose.ui.window.SecureFlagPolicy
    var secureFlagPolicy: Boolean? = null

    fun build(): DialogOptions {
        return DialogOptions(
            alignment,
            dismissOnBackPress,
            dismissOnClickOutside,
            usePlatformDefaultWidth,
            usePlatformInsets,
            scrimColor, decorFitsSystemWindows, secureFlagPolicy, enter, exit
        )
    }
}