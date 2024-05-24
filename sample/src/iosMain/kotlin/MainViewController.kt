import com.erolc.mrouter.MRouter
import com.erolc.mrouter.mRouterComposeUIViewController
import com.erolc.mrouter.route.platformRoute

fun MainViewController() = mRouterComposeUIViewController {
    MRouter.registerBuilder {
        platformRoute("test", TestUIViewController())
    }
    App()
}