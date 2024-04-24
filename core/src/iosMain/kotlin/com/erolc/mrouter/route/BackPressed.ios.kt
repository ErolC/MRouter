package com.erolc.mrouter.route

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSSelectorFromString
import platform.UIKit.UIApplication

@Composable
actual fun SysBackPressed(body: () -> Unit) {}

@OptIn(ExperimentalForeignApi::class)
@Composable
internal actual fun ExitImpl() {
    //这里是回到主界面，而不是退出app。
    UIApplication.sharedApplication.performSelector(NSSelectorFromString("suspend"))
}