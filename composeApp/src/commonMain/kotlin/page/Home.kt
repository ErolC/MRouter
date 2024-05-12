package page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erolc.mrouter.scope.LifecycleObserver
import com.erolc.mrouter.scope.rememberLazyListState
import com.erolc.mrouter.utils.Page
import com.erolc.mrouter.utils.isDesktop
import com.erolc.mrouter.utils.loge
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

data class Future(val name: String, val value: String)

class TestViewModel(holder: SavedStateHandle):ViewModel(){

    val state = mutableStateOf(true)
}

@Composable
fun Home() = Page {
    LifecycleObserver { _, event ->
        loge("tag","home $event")
    }
    val vm = viewModel(TestViewModel::class)
    var state by  vm.state
    loge("tag","$state --- ")
    val list = remember {
        listOf(
            Future("普通的路由跳转", "normal"),
            Future("带参的路由跳转", "arg"),
            Future("带返回值的路由跳转", "return"),
            Future("局部路由", "panel"),
            Future("列表详情", "list"),
            Future("共享元素", "share"),
            Future("动画", "anim"),
            Future("手势", "gesture"),
            if(isDesktop) Future("多窗口","window") else null
        ).filterNotNull()
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
                            "normal" -> {
                                state = false
                                route("first")
                            }
                            "arg" -> route("first?key=arg")
                            "return" -> route("first?key=return")
                            "panel" -> route("panel")
                            "share" -> route("share")
                            "anim" -> route("anim")
                            "list" -> route("list")
                            "gesture" -> route("gesture")
                            "window" ->route("first"){
                                window("secondWindow","这是第二个窗口")
                            }
                        }
                    }) {
                        Text(it.name)
                    }

                }

            }
        }

    }

}