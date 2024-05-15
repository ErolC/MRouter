package com.erolc.mrouter.model

import androidx.core.bundle.Bundle
import platform.UIKit.UIViewController

data class IosRouteSource(val rootVC: UIViewController, val args: Bundle, val result: Bundle)