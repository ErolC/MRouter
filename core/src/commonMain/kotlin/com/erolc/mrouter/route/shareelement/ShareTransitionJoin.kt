package com.erolc.mrouter.route.shareelement

import androidx.compose.runtime.Composable
import com.erolc.mrouter.utils.ShareAnimBody
import com.erolc.mrouter.utils.Sharing

/**
 * 用于连接transition和anim的累
 */
class ShareTransitionJoin<T>(
     private val transition: ShareTransition,
     private val index: Int,
     private val sharing: Sharing.(preValue: T, currentValue: T) -> T = { _, current -> current }
) {
    @Composable
    infix fun with(anim: ShareAnimBody<T>) = transition.styleCore(index, sharing, anim)
}



