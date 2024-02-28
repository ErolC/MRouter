import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

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
            scope.setResult("result" to 1233)
            scope.backPressed()
        }) {
            Text("back")
        }
        Button(onClick = {
            greetingText = "Compose: ${Greeting().greet()}"
            showImage = !showImage
        }) {
            Text(greetingText)
        }
        AnimatedVisibility(showImage) {
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
    Column(
        Modifier.fillMaxSize().background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            scope.setResult("result" to 1233)
            scope.backPressed()
        }) {
            Text("back:${args.getData<Int>("key")}")
        }
        Button(onClick = {
            scope.route("greet") {
            }
        }) {
            Text(greetingText)
        }
    }
}


@Composable
fun Home() {
    Exit(true)
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
        modifier = Modifier.fillMaxSize().background(Color.Green)
    ) {
        items(list) {
            Text(it, Modifier.fillMaxWidth().padding(10.dp).clickable {
                scope.route("second?key=123") {
//                    dialog {
//                        enter = slideInVertically()
//                        exit = slideOutVertically()
//                    }
//                    window("second", "second"){
//                        alwaysOnTop = true
//                    }
                    transform {
//                        enter = expandHorizontally()
//                        exit = shrinkHorizontally()
                        enter = fadeIn()
//                        prev = fadeOut()
                        exit = fadeOut()
                    }
                    onResult {
                        log("ATG", "data:${it.getDataOrNull<Int>("result")}")
                    }
                }
            }, fontSize = 20.sp)
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun PageTemplate(onBack: (() -> Unit)?, content: @Composable () -> Unit) {
    val squareSize = LocalWindowScope.current.windowSize.value.width.size
    val swipeableState = rememberSwipeableState(0) {
        onBack != null
    }
    DisposableEffect(swipeableState.currentValue) {
        if (swipeableState.currentValue == 1) {
            onBack?.invoke()
        }
        onDispose {}
    }
    val sizePx = with(LocalDensity.current) { squareSize.toPx() }
    val anchors = mapOf(0f to 0, sizePx to 1) // Maps anchor points (in px) to states
    Box {
        Box(
            modifier = Modifier.fillMaxHeight().width(10.dp).swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
        )
        Box(modifier = Modifier.offset {
            if (onBack != null)
                IntOffset(swipeableState.offset.value.roundToInt(), 0)
            else
                IntOffset.Zero

        }) {
            content()
        }
    }
}
