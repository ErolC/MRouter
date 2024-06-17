package com.erolc.mrouter.route.transform.share

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Rect
import com.erolc.mrouter.route.transform.TransformWrap
import com.erolc.mrouter.utils.ShareState

/**
 * 共享手势，目前共享过程中不支持手势
 * @param keys 在该次页面转换过程中共享的控件的key
 */
abstract class ShareTransformWrap( val shareAnimationSpec: FiniteAnimationSpec<Rect>,vararg val keys: Any) : TransformWrap()