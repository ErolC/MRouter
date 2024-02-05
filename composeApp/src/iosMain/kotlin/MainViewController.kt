import com.erolc.mrouter.MRouterUIViewController
import com.erolc.mrouter.lifecycle.UIApplicationBackgroundDelegate
import com.erolc.mrouter.lifecycle.UIApplicationBackgroundDelegateImpl

/**
 * 由于放到Mrouter里面将无法
 */
object MyUIApplicationBackgroundDelegate :
    UIApplicationBackgroundDelegate by UIApplicationBackgroundDelegateImpl

fun MainViewController() = MRouterUIViewController(MyUIApplicationBackgroundDelegate) {
    App()
}

