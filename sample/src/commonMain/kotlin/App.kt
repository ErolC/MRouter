import androidx.compose.material.*
import androidx.compose.runtime.*
import com.erolc.mrouter.RouteHost
import com.erolc.mrouter.register.page
import page.*
import page.Target

@Composable
fun App(typography: Typography = MaterialTheme.typography) {
    MaterialTheme(typography = typography) {
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
            page("gesture") { Gesture() }
            page("target") { Target() }
            page("search1") { Search1() }
        }
    }
}
