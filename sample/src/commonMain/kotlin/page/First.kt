package page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.erolc.mrouter.scope.LifecycleObserver
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.utils.Page
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.utils.rememberInPage

@Composable
fun First() = Page {
    LifecycleObserver { _, event ->
        loge("tag", "first - $event")
    }
    val args = rememberArgs()
    Column(modifier = Modifier.fillMaxWidth().safeContentPadding()) {
        var result by rememberInPage("data") { mutableStateOf("") }
        Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
            backPressed()
        }) {
            Text("回退:${args.getString("key","default")}")
        }
        Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
            val key = args.getString("key")
            var route = "first?key=success"
            if (key == "arg") route += "?value=routeData"
            route(route) {
                argBuild {
                    if (key == "arg") {
                        putString("value1", "otherData")
                    }
                    if (key == "return") {
                        putBoolean("return", true)
                    }
                    if (key == "return") {
                        onResult {
                            result = it.getString("back_data", "")
                            loge("tag", "result:$result")
                        }
                    }
                }
            }
        }) {
            val key = args.getString("key")
            Text("前往下一个页面${if (key == "arg") "并携带数据" else ""}")
        }
        val key = args.getString("key")
        if (key == "return") {
            Text("这是由Second页面回传的数据：${result}")
        }
    }
}