import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.erolc.lifecycle.Lifecycle
import com.erolc.lifecycle.SystemLifecycle
import com.erolc.lifecycle.addEventObserver
import com.erolc.mrouter.RouteHost
import com.erolc.mrouter.backstack.LocalWindowScope
import com.erolc.mrouter.register.page
import com.erolc.mrouter.route.BackInterceptor
import com.erolc.mrouter.route.Exit
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.scope.rememberLazyListState
import com.erolc.mrouter.utils.log
import com.erolc.mrouter.utils.loge
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt
import kotlin.time.Duration

@Composable
fun App() {
    MaterialTheme {
        RouteHost("home") {
            page("greet") {
                GreetingPage()
            }
            page("second") {
                Second()
            }
            page("home") {
                Home()
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun GreetingPage() {
    var greetingText by remember { mutableStateOf("Hello World!") }
    var showImage by remember { mutableStateOf(false) }
    val scope = LocalPageScope.current
    val args = rememberArgs()
    Column(
        Modifier.background(Color.White).fillMaxSize().zIndex(20f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            scope.setResult("result" to 3444)
            scope.backPressed()
        }) {
            Text("back111")
        }
        Button(onClick = {
            greetingText = "Compose: ${Greeting().greet()}"
            showImage = !showImage
        }) {
            Text(greetingText)
        }
        AnimatedVisibility(
            showImage,
            modifier = Modifier,
            enter = androidx.compose.animation.slideInHorizontally(),
            exit = androidx.compose.animation.slideOutHorizontally()
        ) {
            Image(
                painterResource(DrawableResource("compose-multiplatform.xml")),
                null
            )
        }
    }
}

@Composable
fun Second() {
    var greetingText by remember { mutableStateOf("Hello World!") }
    val scope = LocalPageScope.current
    val args = rememberArgs()
    scope.lifecycle.addEventObserver { source, event ->
        when (event) {
            Lifecycle.Event.ON_DESTROY -> {
                scope.setResult("result" to 1233)
            }

            else -> {}
        }
    }
    Column(
        Modifier.background(Color.White), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            scope.backPressed()
        }) {
            Text("back:${args.getDataOrNull<Int>("key")}")
        }
        Button(onClick = {
            scope.route("greet") {
                onResult {
                    log("ATG", "data____:${it.getDataOrNull<Int>("result")}")
                }
            }
        }) {
            Text(greetingText)
        }

        Exit {
            Button(onClick = {
                scope.backPressed()
            }) {
                Text("是否后退")
            }
        }
    }
}


@Composable
fun Home() {
    val scope = LocalPageScope.current
    val list = remember {
        listOf(
            "1111111552222221116666633333344",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "0",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "24",
            "25",
            "26",
            "27",
            "34",
            "35",
            "36",
            "37",
            "44",
            "45",
            "46",
            "47"
        )
    }
    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier.fillMaxSize().background(Color.Green).padding(top = 47.dp)
    ) {
        items(list) {

            Text(it, Modifier.fillMaxWidth().padding(10.dp).clickable {
                scope.route("second?key=123") {
//                    dialog {
//                        enter = slideInVertically()
//                        exit = slideOutVertically()
//                    }
                    window("second", "second")
//                    transform {
//                        enter = fadeIn()+ slideInHorizontally()
//                        prevPause = fadeOut()+ slideOutHorizontally { it }
//                    }
//                        transform = normal()
                    onResult {
                        log("ATG", "data:${it.getDataOrNull<Int>("result")}")
                    }
                }
            }, fontSize = 20.sp)
        }
    }
}
