package com.erolc.mrouter.model

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Rect
import com.erolc.mrouter.utils.PreShare
import com.erolc.mrouter.utils.ShareState

/**
 * 一个共享组
 * @param start 开始页面的共享元素
 * @param end 结束页面的共享元素
 */
@Immutable
data class ShareElementGroup(val start: ShareElement, val end: ShareElement, val key: Any)

/**
 * 共享条目
 */
@Immutable
data class ShareEntry(
    val groups: List<ShareElementGroup>,
    val shareAnimationSpec: FiniteAnimationSpec<Rect>,
    val startAddress: String,
    val endAddress: String,
    val keys: String,
    val resetState :MutableState<ShareState> = mutableStateOf(PreShare)
){
    fun equalTag(key: String,startAddress: String,endAddress: String) = this.keys == key && this.startAddress == startAddress && this.endAddress == endAddress
    fun equalEntry(entry: ShareEntry?) = entry != null && entry.keys == keys && entry.startAddress == startAddress && entry.endAddress == endAddress
}