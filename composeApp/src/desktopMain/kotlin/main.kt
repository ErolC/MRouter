import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.erolc.mrouter.mRouterApplication

fun main() = mRouterApplication {
    App()
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}