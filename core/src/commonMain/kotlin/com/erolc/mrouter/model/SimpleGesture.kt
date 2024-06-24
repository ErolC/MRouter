package com.erolc.mrouter.model

import androidx.compose.foundation.gestures.Orientation
import com.erolc.mrouter.route.transform.GestureModel

/**
 * @param gestureModel：[GestureModel.None]：无手势；[GestureModel.Local]:局部手势，仅在页面左侧拥有15dp宽度的手势区域，如果是android则会现在在左侧中间200dp高度内；[GestureModel.Full]：全面手势，在页面任何地方都可使用手势后退；[GestureModel.Both]：相当于同时设置了[GestureModel.Full]和[GestureModel.Local]；
 * @param orientation:方向
 */
data class SimpleGesture(
    val gestureModel: GestureModel,
    val orientation: Orientation = Orientation.Horizontal
)