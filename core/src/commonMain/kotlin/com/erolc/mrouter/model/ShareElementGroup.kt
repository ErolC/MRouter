package com.erolc.mrouter.model

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import com.erolc.mrouter.route.shareele.ShareState

/**
 * 一个共享组
 * @param start 开始页面的共享元素
 * @param end 结束页面的共享元素
 */
data class ShareElementGroup(val start: ShareElement, val end: ShareElement)

/**
 * 共享条目
 */
data class ShareEntry(val groups: List<ShareElementGroup>, val transitionSpec: @Composable Transition.Segment<ShareState>.() -> FiniteAnimationSpec<Rect>,val keys:String)