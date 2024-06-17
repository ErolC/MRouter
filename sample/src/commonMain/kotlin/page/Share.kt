package page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.route.shareelement.Element
import com.erolc.mrouter.route.transform.share
import com.erolc.mrouter.utils.*


@Composable
fun Share() = Page {
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
                    Text("共享控件", Modifier.align(Alignment.CenterStart))
                }
            }
        }
        Spacer(Modifier.weight(1f))

    }
}

@Composable
fun Search() = Page {
    Row(Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        Button(onClick = {
            backPressed()
        }) {
            Text("back")
        }
        Element(
            "search",
            Modifier.padding(20.dp).weight(4f).height(50.dp),
            listOf(100.dp, Color.Red, "共享控件")
        ) {
            var text by remember { mutableStateOf(getValue<String>(2)) }
            val corner by getStyle<Dp>(0) with animateDp()
            val color by getStyle(1, sharing = ::sharing, animate = animateColor())
            val alpha by getTextStyle(2) {
                text = it
            }
            Surface(shape = RoundedCornerShape(corner), color = color) {
                Box(Modifier.fillMaxSize().clickable {
                    route("search1") {
                        transform = share(
                            "search"
                        )
                    }
                }) {
                    Text(text, Modifier.align(Alignment.CenterStart).alpha(alpha))
                }
            }
        }

        Element("label", modifier = Modifier.weight(1f).height(20.dp)) {
            Text("second", Modifier.clickable {
                route("search1") {
                    transform = share(
                        "search", "label"
                    )
                }
            })
        }
        Spacer(Modifier.weight(1f))
    }
}


@Composable
fun Search1() = Page {
    Row(Modifier.fillMaxSize()) {
        Spacer(Modifier.weight(1f))
        Element("label", modifier = Modifier.weight(1f).height(20.dp)) {
            Text("second", Modifier.clickable {
                updateElement("label")
            })
        }
        Element(
            "search",
            Modifier.padding(100.dp).weight(4f).height(50.dp),
            listOf(30.dp, Color.Gray, "测试文本")
        ) {
            var text by remember { mutableStateOf(getValue<String>(2)) }
            val corner by getStyle<Dp>(0) with animateDp()
            val color by getStyle(1, sharing = ::sharing, animate = animateColor())

            val alpha by getTextStyle(2) {
                text = it
            }
            Surface(shape = RoundedCornerShape(corner), color = color) {
                Box(Modifier.fillMaxSize().clickable {
                    backPressed()
                }) {
                    Text(text, Modifier.align(Alignment.CenterStart).alpha(alpha))
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}