package page

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.platform.loge
import com.erolc.mrouter.route.shareelement.Element
import com.erolc.mrouter.route.transform.share
import com.erolc.mrouter.utils.*


@Composable
fun Share() = Page {
    loge("tag", "Share")
    Row(Modifier.fillMaxSize()) {
        Button(onClick = {
            backPressed()
        }) {
            Text("back")
        }
        Spacer(Modifier.weight(1f))
        Element(
            "search",
            Modifier.padding(10.dp).weight(3f).height(50.dp),
            listOf(10.dp, Color.Blue)
        ) {
            val corner by getStyle(0, animate = animateDp())
            val color by getStyle(1, animate = animateColor())
            Surface(shape = RoundedCornerShape(corner), color = color) {
                Box(modifier = Modifier.fillMaxSize().clickable {
                    route("search") {
                        transform = share("search")
                    }
                }) {
                    Text("search", Modifier.align(Alignment.CenterStart))
                }
            }
        }
        var showLabel by rememberInPage("showLabel") {
            mutableStateOf(false)
        }
        onUpdateElement {
            loge("tag", "ddddd")
            showLabel = true
        }
        if (showLabel)
            Element("label", modifier = Modifier.weight(1f).height(20.dp)) {
                Text("text")
            }
        Spacer(Modifier.weight(1f))

    }
}

@Composable
fun Search() = Page {
    loge("tag", "Search")
//    EventObserver { lifecycleOwner, event ->
//        logi("tag","$lifecycleOwner $event")
//    }
    Row(Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        Element("label", modifier = Modifier.weight(1f).height(20.dp)) {
            Text("text", Modifier.clickable {
                backPressed()
            })
        }
        Element(
            "search",
            Modifier.padding(20.dp).weight(4f).height(50.dp),
            listOf(100.dp, Color.Red)
        ) {
            val corner by getStyle<Dp>(0) with animateDp()
            val color by getStyle(1, animate = animateColor())
            Surface(shape = RoundedCornerShape(corner), color = color) {
                Box(Modifier.fillMaxSize().clickable {
//                    backPressed()
                    route("search1") {
                        transform = share(
                            "search"
                        )
                    }
                }) {
                    Text("search", Modifier.align(Alignment.CenterStart))
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}


@Composable
fun Search1() = Page {
    loge("tag", "Search1")
//    EventObserver { lifecycleOwner, event ->
//        logi("tag","$lifecycleOwner $event")
//    }
    Row(Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        Element("label", modifier = Modifier.weight(1f).height(20.dp)) {
            Text("text", Modifier.clickable {
                updateElement("label")
                backPressed()
            })
        }
        Element("search", Modifier.padding(100.dp).weight(4f).height(50.dp), listOf(30.dp)) {
            val corner by getStyle<Dp>(0) with animateDp()

            Surface(shape = RoundedCornerShape(corner), color = Color.Gray) {
                Box(Modifier.fillMaxSize().clickable {
                    backPressed()
                }) {
                    Text("search", Modifier.align(Alignment.CenterStart))
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}