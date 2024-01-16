import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erolc.mrouter.RouteHost
import com.erolc.mrouter.register.page
import com.erolc.mrouter.route.BackInterceptor
import com.erolc.mrouter.route.Exit
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.scope.rememberLazyListState
import com.erolc.mrouter.utils.log
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    MaterialTheme {
        RouteHost("home") {
            page("greet") {
                GreetingPage()
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
        Modifier.fillMaxSize().background(Color.Gray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            scope.setResult("result" to 1233)
            scope.backPressed()
        }) {
            Text("back:${args.getData<Int>("key")}")
        }
        Button(onClick = {
            greetingText = "Compose: ${Greeting().greet()}"
            showImage = !showImage
        }) {
            Text(greetingText)
        }
        AnimatedVisibility(showImage) {
            Image(
                painterResource("compose-multiplatform.xml"),
                null
            )
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
                scope.route("greet?key=123") {
                    window("second","第二窗体")
                    onResult {
                        log("ATG", "data:${it.getDataOrNull<Int>("result")}")
                    }
                }
            }, fontSize = 20.sp)
        }
    }
}
