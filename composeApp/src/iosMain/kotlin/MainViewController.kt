import com.erolc.lifecycle.LifeUIViewController

/**
 * 由于放到Mrouter里面将无法
 */
fun MainViewController() = LifeUIViewController {
    App()
}

