import com.erolc.lifecycle.LifecycleUIViewController

/**
 * 由于放到Mrouter里面将无法
 */
fun MainViewController() = LifecycleUIViewController {
    App()
}

