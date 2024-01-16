package com.erolc.mrouter.window

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.model.WindowOptions

class WindowOptionsBuilder {

    var icon: Painter? = null
    var resizable: Boolean = true
    var alwaysOnTop: Boolean = false
    var minimumSize: DpSize = DpSize(405.dp, 720.dp) // 9:16
    var maximumSize: DpSize = DpSize.Unspecified
    var size: DpSize = DpSize(800.dp, 720.dp)
    var position: DpOffset = DpOffset.Zero
    var alignment: Alignment? = Alignment.Center

    fun build(id: String,title:String): WindowOptions {
        return WindowOptions(
            id,
            title,
            icon,
            resizable,
            alwaysOnTop,
            minimumSize,
            maximumSize,
            size,
            position,
            alignment
        )
    }
}