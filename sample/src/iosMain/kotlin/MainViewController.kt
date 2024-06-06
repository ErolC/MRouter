import androidx.compose.ui.window.ComposeUIViewController
import com.erolc.mrouter.MRouter
import com.erolc.mrouter.route.platformRoute

fun MainViewController() = ComposeUIViewController {
    MRouter.register {
        platformRoute("platform", TestUIViewController())
    }
    App()
}

