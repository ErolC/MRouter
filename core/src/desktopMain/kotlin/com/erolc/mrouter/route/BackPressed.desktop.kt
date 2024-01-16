package com.erolc.mrouter.route

import androidx.compose.runtime.Composable
import com.erolc.mrouter.LocalApplication

@Composable
actual fun SysBackPressed(body: () -> Unit) {

}

@Composable
internal actual fun ExitImpl() {
    val application =
        LocalApplication.current
            ?: throw RuntimeException("请使用mRouterApplication替代原本的application")
    application.exitApplication()
}