package com.erolc.mrouter.route.shareele

import androidx.compose.runtime.Composable

@Composable
fun ShareEle(tag: String, content: @Composable () -> Unit) {
    val controller = LocalShareEleController.current

}