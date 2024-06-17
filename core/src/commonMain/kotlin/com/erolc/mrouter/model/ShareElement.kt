package com.erolc.mrouter.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Rect
import com.erolc.mrouter.route.shareelement.ShareTransition
import com.erolc.mrouter.utils.Init
import com.erolc.mrouter.utils.ShareState
import kotlinx.coroutines.flow.StateFlow

/**
 * 一个共享元素
 * @param key 元素的标识
 * @param content 元素的compose
 * @param address 元素所在地址
 * @param position element的位置和尺寸
 * @param styles 样式列表，给共享元素内的元素使用
 */
@Immutable
data class ShareElement internal constructor(
    val key: Any,
    val content: @Composable ShareTransition.() -> Unit,
    val address: String,
    val position: StateFlow<Rect>,
    val styles:List<Any>,
    ) {
    val tag get() = "${address}_${key.hashCode()}"

    /**
     * 该共享元素的状态
     */
    internal val _state = mutableStateOf<ShareState>(Init)
    internal val state:State<ShareState> get() = _state
}