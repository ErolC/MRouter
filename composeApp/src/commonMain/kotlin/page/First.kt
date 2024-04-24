package page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.erolc.mrouter.Constants
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.utils.Page
import com.erolc.mrouter.utils.rememberInPage

@Composable
fun First() = Page {
    val args = rememberArgs()
    Column(modifier = Modifier.fillMaxWidth().safeContentPadding()) {
        var result by rememberInPage("data") { mutableStateOf("") }
        Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
            backPressed()
        }) {
            Text("回退:${this@Page.name}")
        }
        Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
            val key = args.getData<String>("key")
            var route = "second"
            if (key == "arg") route += "?value=routeData"
            route(route) {
                window(Constants.DEFAULT_WINDOW)
                if (key == "arg") {
                    arg("value1", "otherData")
                }
                if (key == "return") {
                    arg("return", "true")
                    onResult {
                        result = it.getData<String>("back_data")
                    }
                }
            }
        }) {
            val key = args.getData<String>("key")
            Text("前往下一个页面${if (key == "arg") "并携带数据" else ""}")
        }
        val key = args.getData<String>("key")
        if (key == "return") {
            Text("这是由Second页面回传的数据：${result}")
        }
    }
}