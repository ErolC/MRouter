package com.erolc.mrouter.model

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * @param id Window的id
 * @param title window的标题
 * @param icon  Icon in the titlebar of the window (for platforms which support this). On macOs individual windows can't have a separate icon. To change the icon in the Dock, set it via iconFile in build.gradle (https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution#platform-specific-options)
 * @param resizable 用户是否可以改变窗体大小
 * @param alwaysOnTop 该窗体是否永远置于所有窗体上方
 * @param minimumSize Window最小尺寸
 * @param maximumSize window的最大尺寸
 * @param size window初始尺寸
 * @param position window位置
 * @param alignment 对齐方式，比如说window居中：[Alignment.Center],请注意，在[alignment] 设置了值之后，position将失效
 */
data class WindowOptions(
    val id: String,
    val title: String,
    val icon: Painter? = null,
    val resizable: Boolean = true,
    val alwaysOnTop: Boolean = false,
    val minimumSize: DpSize = DpSize(405.dp, 720.dp), // 9:16
    val maximumSize: DpSize = DpSize.Unspecified,
    val size: DpSize = DpSize(800.dp, 720.dp),
    val position: DpOffset = DpOffset.Zero,
    val alignment: Alignment? = Alignment.Center
)