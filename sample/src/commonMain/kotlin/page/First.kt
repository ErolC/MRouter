package page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.erolc.mrouter.platform.log
import com.erolc.mrouter.scope.LifecycleObserver
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.utils.Page
import com.erolc.mrouter.platform.loge

@Composable
fun First() = Page {
    LifecycleObserver { _, event ->
        loge("tag", "first - $event")
    }
    val args = rememberArgs()
    Column(modifier = Modifier.fillMaxWidth().safeContentPadding()) {
        val result = rememberSaveable {
            val data = mutableStateOf("")
            log("tag", "init_________:$data")
            data
        }
        Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
            backPressed()
        }) {
            Text("回退")
        }

        Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
            val key = args.getString("key")
            var route = "second"
            if (key == "arg") route += "?value=routeData"
            route(route) {
                argBuild {
                    if (key == "arg") {
                        putString("value1", "otherData")
                    }
                    if (key == "return") {
                        putBoolean("return", true)
                    }

                }

                if (key == "return") {
                    onResult {
                        result.value = it.getString("back_data", "")
                        loge("tag", "result:${result.value} ::${result}")
                    }
                }
            }
        }) {
            val key = args.getString("key")
            Text("前往下一个页面${if (key == "arg") "并携带数据" else ""}")
        }
        loge("tag", "first__:$result")
        val key = args.getString("key")
        if (key == "return") {
            Text("这是由Second页面回传的数据：${result.value}")
        }
    }
}