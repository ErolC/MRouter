import com.erolc.mrouter.MRouterUIViewController
import com.erolc.lifecycle.UIApplicationBackgroundDelegate
import com.erolc.lifecycle.UIApplicationBackgroundDelegateImpl

/**
 * 由于放到Mrouter里面将无法
 */
object SwitchBackgroundDelegate :
    UIApplicationBackgroundDelegate by UIApplicationBackgroundDelegateImpl

fun MainViewController() = MRouterUIViewController(SwitchBackgroundDelegate) {
    App()
}

