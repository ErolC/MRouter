package com.erolc.mrouter.route

import androidx.compose.runtime.Composable
import com.erolc.mrouter.backstack.entry.LocalWindowScope

@Composable
actual fun SysBackPressed(body: () -> Unit) {

}

@Composable
internal actual fun ExitImpl() {
    LocalWindowScope.current.close()
}