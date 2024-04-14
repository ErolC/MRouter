package page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.scope.rememberLazyListState
import com.erolc.mrouter.utils.Page

data class Future(val name: String, val value: String)

@Composable
fun Home() = Page {

    val list = remember {
        listOf(
            Future("普通的路由跳转", "normal"),
            Future("带参的路由跳转", "arg"),
            Future("带返回值的路由跳转", "return"),
            Future("局部路由", "panel"),
            Future("共享元素", "share"),
            Future("动画", "anim"),
            Future("手势", "gesture"),
        )
    }
    Row(modifier = Modifier.fillMaxWidth()) {
        LazyColumn(
            state = rememberLazyListState("list"),
            modifier = Modifier.fillMaxSize().weight(1f).background(Color.White)
        ) {
            items(list) {
                Column(Modifier.fillMaxWidth()) {
                    Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), onClick = {
                        when (it.value) {
                            "normal" -> route("first")
                            "arg" -> route("first?key=arg")
                            "return" -> route("first?key=return")
                            "panel" -> route("panel")
                            "share" -> route("share")
                            "anim" -> route("anim")
                        }
                    }) {
                        Text(it.name)
                    }

                }

            }
        }

    }

}