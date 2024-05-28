package com.erolc.mrouter.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import com.erolc.mrouter.register.Register
import com.erolc.mrouter.window.windowMenu


/**
 * @author erolc
 * @since 2024/4/15 10:27
 *
 * 窗口的菜单，需要在[Register]使用[windowMenu]进行注册才可使用
 * ```
 * windowMenu(windowId = Constants.defaultWindow) {
 *     MenuBar {
 *         Menu("Home"){
 *            Item("File", onClick = {})
 *         }
 *     }
 * }
 * ```
 * @param id window的唯一标识
 * @param menu 菜单的compose
 */
@SinceKotlin("1.0")
data class WindowMenu(val id: String, val menu: @Composable FrameWindowScope.() -> Unit)