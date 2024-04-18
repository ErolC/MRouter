import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.erolc.mrouter.mRouterApplication

fun main() = mRouterApplication {
    App()
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}