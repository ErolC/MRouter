package page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import com.erolc.mrouter.PanelHost
import com.erolc.mrouter.scope.LifecycleObserver
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.utils.Page
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.rememberPanelState
import com.erolc.mrouter.route.transform.modal
import com.erolc.mrouter.utils.rememberInPage

data class Content(val name: String, val id: Int)

@Composable
fun ListDetail() = Page {
    val items = rememberInPage("") {
        (0..100).map { Content("item$it", it) }
    }

    Row {
        Column(modifier = Modifier.weight(1f).zIndex(1f)) {
            Button(onClick = {
                backPressed()
            }) {
                Text("back")
            }
            LazyColumn(modifier = Modifier.background(Color.Blue)) {
                items(items) {
                    Text(it.name, Modifier.fillMaxWidth().clickable {
                        route("local:detail/${it.id}L")
                    })
                }
            }

        }
        val panelState = rememberPanelState()
        PanelHost(
            startRoute = "detail/0L",
            modifier = Modifier.fillMaxSize().weight(2f),
            panelState = panelState,
            onPanelChange = {
                loge("tag", "isAttach:$it")
            })
    }
}

@Composable
fun Detail() = Page {
    val args = rememberArgs()
    val id = args.getLong("id")
    Text("detail:${id}", modifier = Modifier.clickable { backPressed() })
}

