import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.window.CanvasBasedWindow
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.readResourceBytes
import androidx.compose.material.Typography
import com.erolc.mrouter.MRouter
import com.erolc.mrouter.route.platformRoute

//通过引入中文字体解决中文显示问题
@OptIn(InternalResourceApi::class)
suspend fun loadFont(): FontFamily {
    return FontFamily(
        Font(
            identity = "normal",
            data = readResourceBytes("fonts/SmileySans-Oblique.otf"),
            weight = FontWeight.Normal
        ),
    )
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    MRouter.registerBuilder {
        platformRoute("platform","https://www.baidu.com")
    }
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        var typography by remember { mutableStateOf<Typography?>(null) }
        LaunchedEffect(Unit) {
            val font = loadFont()
            typography = Typography(defaultFontFamily = font)
        }
        typography?.let { App(typography = it) }
    }
}