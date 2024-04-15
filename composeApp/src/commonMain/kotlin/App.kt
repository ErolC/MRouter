import androidx.compose.material.*
import androidx.compose.runtime.*
import com.erolc.mrouter.RouteHost
import com.erolc.mrouter.register.page
import page.*

@Composable
fun App() {
    MaterialTheme {
        RouteHost("home") {
            page("home") { Home() }
            page("first") { First() }
            page("second") { Second() }
            page("panel") { PanelDemo() }
            page("share") { Share() }
            page("search") { Search() }
            page("anim") { Anim() }
            page("list") { ListDetail() }
            page("detail") { Detail() }
        }
    }
}
