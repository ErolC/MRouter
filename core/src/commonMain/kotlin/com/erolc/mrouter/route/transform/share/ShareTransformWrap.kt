package com.erolc.mrouter.route.transform.share

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.ui.geometry.Rect
import com.erolc.mrouter.route.transform.GestureModel
import com.erolc.mrouter.route.transform.TransformWrap

/**
 * 共享手势
 * @param keys 在该次页面转换过程中共享的控件的key
 */
internal abstract class ShareTransformWrap(
    val shareAnimationSpec: FiniteAnimationSpec<Rect>,
    gestureModel: GestureModel,
    vararg val keys: Any
) : TransformWrap(gestureModel)