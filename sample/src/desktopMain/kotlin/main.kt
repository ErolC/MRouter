import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.MenuBar
import com.erolc.mrouter.MRouter
import com.erolc.mrouter.mRouterApplication
import com.erolc.mrouter.window.windowMenu
import com.formdev.flatlaf.FlatIntelliJLaf

fun main() = mRouterApplication {
    FlatIntelliJLaf.setup()
    MRouter.register {
        windowMenu {
            MenuBar {
                Menu("Home") {
                    Item("File", onClick = {})
                }
            }
        }
    }
    App()
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}