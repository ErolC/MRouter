package com.erolc.mrouter.model

import androidx.core.bundle.Bundle
import platform.UIKit.UIViewController

/**
 * @param rootVC 由RegisterBuilder.setViewController进行注册的根VC
 * @param args 由compose跳转VC时传递的数据
 * @param result 由VC回退到compose时传递的数据
 */
data class IosRouteSource(val rootVC: UIViewController, val args: Bundle,val onResult: (Bundle) -> Unit)