package com.erolc.mrouter.model

import androidx.compose.foundation.gestures.Orientation
import com.erolc.mrouter.route.transform.GestureModel

data class ShareGesture(
    val gestureModel: GestureModel,
    val orientation: Orientation
)