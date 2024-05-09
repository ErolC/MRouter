import androidx.compose.ui.window.ComposeUIViewController

/**
 * 由于放到Mrouter里面将无法
 */
fun MainViewController() = ComposeUIViewController {
    App()
}

