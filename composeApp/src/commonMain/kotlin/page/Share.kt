package page

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.route.shareele.Element
import com.erolc.mrouter.route.shareele.ShareState
import com.erolc.mrouter.route.transform.shareEle
import com.erolc.mrouter.scope.EventObserver
import com.erolc.mrouter.utils.*

/**
 * 建议以这种方式实现
 */
@Composable
fun Transition<ShareState>.padding(isStart:Boolean) = animateDp { it.transform(isStart,0.dp,10.dp) }

@Composable
fun Share() = Page {
    Row(Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        Element("search", Modifier.padding(10.dp).weight(3f).height(50.dp)) {
            val corner by animateDp {
                it.startTransform(100.dp,10.dp)
            }
            val padding by padding(true)

            Surface(shape = RoundedCornerShape(corner), color = Color.Gray) {
                Box(modifier = Modifier.fillMaxSize().clickable {
                    route("search") {
                        transform = shareEle("search","label")
                    }
                }) {
                    Text("search", Modifier.padding(padding).align(Alignment.CenterStart))
                }
            }
        }
        Element("label",  modifier = Modifier.weight(1f).height(20.dp)){
            Text("text")
        }
        Spacer(Modifier.weight(1f))

    }
}

@Composable
fun Search() = Page {
    EventObserver { lifecycleOwner, event ->
        logi("tag","$lifecycleOwner $event")
    }
    Row(Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        Element("label", modifier = Modifier.weight(1f).height(20.dp)){
            Text("text")
        }
        Element("search", Modifier.padding(20.dp).weight(4f).height(50.dp)) {
            val corner by animateDp {
                it.endTransform(100.dp,10.dp)
            }
            val padding by padding(false)

            Surface(shape = RoundedCornerShape(corner), color = Color.Gray) {
                Box(Modifier.fillMaxSize().clickable {
                    backPressed()
                }) {
                    Text("search", Modifier.padding(padding).align(Alignment.CenterStart))
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}