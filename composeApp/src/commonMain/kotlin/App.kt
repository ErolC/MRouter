import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.createChildTransition
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.addEventObserver
import com.erolc.mrouter.AutoPanel
import com.erolc.mrouter.PanelHost
import com.erolc.mrouter.RouteHost
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.model.PageConfig
import com.erolc.mrouter.register.page
import com.erolc.mrouter.route.ClearTaskFlag
import com.erolc.mrouter.route.NormalFlag
import com.erolc.mrouter.route.RouteFlag
import com.erolc.mrouter.route.StackFlag
import com.erolc.mrouter.route.shareele.Element
import com.erolc.mrouter.route.shareele.Init
import com.erolc.mrouter.route.transform.modal
import com.erolc.mrouter.route.transform.none
import com.erolc.mrouter.route.transform.normal
import com.erolc.mrouter.route.transform.shareEle
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.addEventObserver
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.scope.rememberLazyListState
import com.erolc.mrouter.utils.log
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.utils.rememberInPage
import com.erolc.mrouter.window.WindowWidthSize
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import page.*

@Composable
fun App() {
    MaterialTheme {
        RouteHost("home") {
            page("home") {
                Home()
            }
            page("first") {
                First()
            }

            page("second") {
                Second()
            }
            page("panel") {
                PanelDemo()
            }
            page("share") {
                Share()
            }
            page("search") {
                Search()
            }
            page("anim") {
                Anim()
            }
        }
    }
}
