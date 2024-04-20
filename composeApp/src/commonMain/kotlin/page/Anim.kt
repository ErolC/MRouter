package page

import androidx.compose.animation.core.animateDp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.scope.EventObserver
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.scope.rememberLazyListState
import com.erolc.mrouter.utils.Page
import com.erolc.mrouter.utils.loge


@Composable
fun Anim() = Page {
    val list = remember {
        listOf(
            Future("slide", "slide"),
            Future("scale", "scale"),
            Future("fade", "fade"),
            Future("expand", "expand"),
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = {
            backPressed()
        }) {
            Text("back")
        }
        LazyColumn(
            state = rememberLazyListState("list"),
            modifier = Modifier.fillMaxSize().weight(1f).background(Color.White)
        ) {
            items(list) {
                Column(Modifier.fillMaxWidth()) {
                    Button(modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp), onClick = {
                        when (it.value) {
                            "slide" -> route("target") {
                                transform {
                                    enter = slideInHorizontally { it }
                                }
                            }

                            "scale" -> route("target") {
                                transform {
                                    enter = scaleIn()
                                }
                            }

                            "fade" -> route("target") {
                                transform {
                                    enter = fadeIn()
                                }
                            }

                            "expand" -> route("target") {
                                transform {
                                    enter = expandVertically()
                                }
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


@Composable
fun Target() = Page(modifier = Modifier.background(Color.Gray)) {
    val args = rememberArgs()
    //页面元素如果希望在页面过程中也有动画的表现，可以使用该方法获得转换过程，
    val transition = rememberTransformTransition()
    val padding by transition.animateDp {
        when (it) {
            PreEnter -> 200.dp// 页面进入
            Resume -> 30.dp// 页面显示
            PostExit -> 10.dp// 页面退出
            is Exiting -> (300 - 300 * it.progress).dp// 页面正在手势的过程中，是在Resume到PostExit的过程中 1-0
            is Pausing -> (300 * it.progress).dp// 页面正在手势的过程中，是在Resume到PauseState的过程中 1-0
            else -> 0.dp
        }
    }
    Column {
        val gesture = args.getData<String>("gesture")
        when (gesture) {
            "normal" -> Text(
                "可将手指或鼠标通过拖拽页面的最左侧实现右滑动后退",
                modifier = Modifier.padding(top = padding)
            )

            "modal" -> Text("可将手指或鼠标通过拖拽页面的最上方实现向下滑动后退")
            else -> Button(onClick = {
                backPressed()
            }, modifier = Modifier.padding(top = padding)) {
                Text("back")
            }
        }

    }
}