import androidx.compose.animation.AnimatedVisibility
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

@Composable
fun App() {
    MaterialTheme {
        RouteHost("greet") {
            page("greet") {
                GreetingPage()
            }
            page("second") {
                Second()
            }
            page("home") {
                Home()
            }
            page("three") {
                ThreePage()
            }
        }
    }
}

@Composable
fun ThreePage() {
    addEventObserver { lifecycleOwner, event ->
        loge("tag", "ThreePage_____$event")
    }
    val scope = LocalPageScope.current
    Column(Modifier.background(Color.White)) {
        Button(onClick = {
            scope.backPressed()
        }) {
            Text("back")
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
    addEventObserver { lifecycleOwner, event ->
        loge("tag", "greet__$event")
    }
    Row {
        Column(
            Modifier.background(Color.Blue).fillMaxSize().weight(1f).zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Element("back", modifier = Modifier.width(100.dp).height(50.dp)) {
                Box(Modifier.fillMaxSize().background(Color.Gray).alpha(0.5f)) {
                    Button(onClick = {
//                scope.setResult("result" to 3444)
//                scope.backPressed()
                        scope.route("second?key=123")
                    }) {
                        Text("back:123")
                    }
                }
            }
            Button(onClick = {
//            greetingText = "Compose: ${Greeting().greet()}"
//            showImage = !showImage
                scope.route("second?key=123") {
//                window(defaultWindow, "greet")
                    transform = shareEle("back")
                    onResult {
                        log("ATG", "data____:${it.getDataOrNull<Int>("result")}")
                    }
                }
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
//        PanelHost(modifier = Modifier.weight(2f), onPanelChange = {
//            loge("tag", "isAttach:$it")
//        })
    }
}

@Composable
fun Second() {
    addEventObserver { lifecycleOwner, event ->
        loge("tag", "second__$event")
    }
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
    var value by remember { mutableStateOf("") }
    Column(
        Modifier.background(Color.Red), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            scope.backPressed()
        }) {
            Text("back:${args.getDataOrNull<Int>("key")}")
        }
        Element("back", modifier = Modifier.width(200.dp).height(100.dp)) {
            Box(Modifier.fillMaxSize().background(Color.Gray).alpha(0.5f)) {
                Button(onClick = {
                    scope.backPressed()
                }) {
                    Text("back:${args.getDataOrNull<Int>("key")}")
                }
            }
        }
        Button(onClick = {
            scope.backPressed()
        }) {
            Text("back:${args.getDataOrNull<Int>("key")}")
        }

//        TextField(value, onValueChange = {
//            value = it
//        })
        Button(onClick = {
            scope.route("three") {
//                window(defaultWindow, "greet")
                transform = modal()
                onResult {
                    log("ATG", "data____:${it.getDataOrNull<Int>("result")}")
                }
            }
        }) {
            Text(greetingText)
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
    Row {
        LazyColumn(
            state = rememberLazyListState("list"),
            modifier = Modifier.fillMaxSize().weight(1f).background(Color.Green)
        ) {
            items(list) {

                Text(it, Modifier.fillMaxWidth().padding(10.dp).clickable {
                    scope.route("second?key=123") {
//                    window("second", "second")
//                    transform {
//                        enter = fadeIn()+ slideInHorizontally()
//                        prevPause = fadeOut()+ slideOutHorizontally { it }
//                    }
                        transform = normal()
                        onResult {
                            log("ATG", "data:${it.getDataOrNull<Int>("result")}")
                        }
                    }
                }, fontSize = 20.sp)
            }
        }

    }

}