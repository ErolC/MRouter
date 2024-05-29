package com.erolc.mrouter

import android.content.Context
import com.erolc.mrouter.route.ResourcePool
import com.erolc.mrouter.route.router.WindowRouter

/**
 * context
 */
internal fun getContext() = ResourcePool.getPlatformRes()[Constants.CONTEXT] as Context
