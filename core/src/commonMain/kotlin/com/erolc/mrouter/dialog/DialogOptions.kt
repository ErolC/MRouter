package com.erolc.mrouter.dialog

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

data class DialogOptions(
    val alignment: Alignment = Alignment.Center,
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
    val usePlatformDefaultWidth: Boolean = true,
    val usePlatformInsets: Boolean = true,
    val scrimColor: Color,
    val decorFitsSystemWindows: Boolean = true,
    val secureFlagPolicy:Boolean? = null,
    val enter:EnterTransition,
    val exit:ExitTransition,
    val isShowDialog: MutableState<Boolean> = mutableStateOf(false)
)