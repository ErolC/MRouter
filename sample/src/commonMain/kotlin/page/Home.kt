package page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.erolc.mrouter.lifecycle.viewModel
import com.erolc.mrouter.scope.LifecycleObserver
import com.erolc.mrouter.scope.rememberLazyListState
import com.erolc.mrouter.utils.Page
import com.erolc.mrouter.platform.isDesktop
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.route.transform.fadeIn
import com.erolc.mrouter.route.transform.modal
import com.erolc.mrouter.route.transform.normal
import com.erolc.mrouter.route.transform.share

data class Future(val name: String, val value: String)

@Composable
fun Home() = Page {
    LifecycleObserver { _, event ->
        loge("tag", "home $event")
    }
    val list = remember {
        listOf(
            Future("路由跳转", "normal"),
            Future("局部路由", "panel"),
            Future("列表详情", "list"),
            Future("共享元素", "share"),
            Future("动画", "anim"),
            Future("手势", "gesture"),
            if (isDesktop) Future("多窗口", "window") else Future("平台界面", "platform")
        )
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(
            state = rememberLazyListState("list"),
            modifier = Modifier.fillMaxSize().weight(1f).background(Color.White)
        ) {
            items(list) {
                Column(Modifier.fillMaxWidth()) {
                    Button(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .padding(top = 10.dp), onClick = {
                            when (it.value) {
                                "normal" -> route("sample/first")
                                "panel" -> route("panel")
                                "share" -> route("share")
                                "anim" -> route("anim")
                                "list" -> route("list")
                                "gesture" -> route("gesture"){
                                    transform = normal()
                                }
                                "window" -> route("sample/first") {
                                    window("secondWindow", "这是第二个窗口")
                                }

                                "platform" -> route("platform")
                            }
                        }) {
                        Text(it.name)
                    }

                }

            }
        }

    }

}