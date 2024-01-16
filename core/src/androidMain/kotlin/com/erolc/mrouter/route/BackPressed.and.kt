package com.erolc.mrouter.route

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
@Composable
actual fun SysBackPressed(body: () -> Unit) {
    BackHandler(onBack = body)
}
@Composable
internal actual fun ExitImpl(){
    val context = LocalContext.current
    (context as? Activity)?.finish()
}