package page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.erolc.mrouter.route.transform.normal
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.rememberLazyListState
import com.erolc.mrouter.utils.log

@Composable
fun Home() {
    val scope = LocalPageScope.current

    val list = remember {
        listOf(
            "普通的路由跳转",
            "带参的路由跳转",
            "带返回值的路由跳转",
            "局部路由",
            "共享元素",
            "动画",
            "手势",
            "生命周期",
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