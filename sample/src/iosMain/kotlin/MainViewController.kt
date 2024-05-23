import com.erolc.mrouter.MRouter
import com.erolc.mrouter.mRouterComposeUIViewController
import com.erolc.mrouter.route.platformRoute

/**
 * 由于放到Mrouter里面将无法
 */
fun MainViewController() = mRouterComposeUIViewController {
    MRouter.registerBuilder {
        platformRoute("test", TestUIViewController())
    }
    App()
}

