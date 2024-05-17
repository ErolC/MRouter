package com.erolc.mrouter

import android.content.Context
import com.erolc.mrouter.route.router.WindowRouter

/**
 * context
 */
internal fun WindowRouter.getContext() = platformRes[Constants.CONTEXT] as Context
