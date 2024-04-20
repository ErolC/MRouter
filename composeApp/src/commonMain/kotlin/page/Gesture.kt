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
import com.erolc.mrouter.scope.rememberLazyListState
import com.erolc.mrouter.utils.Page

@Composable
fun Gesture() = Page {
    val list = remember {
        listOf(
            Future("normal", "normal"),
            Future("modal", "modal")
        )
    }
    val transition = rememberTransformTransition()
    val padding by transition.animateDp {
        when(it){
            PreEnter -> 200.dp
            Resume ->30.dp
            PostExit -> 10.dp
            is Exiting -> (300 * it.progress).dp
            is Pausing -> (300-300 * it.progress).dp
            else -> 0.dp
        }
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = {
            backPressed()
        },Modifier.padding(top = padding)){
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
                            "normal" -> route("target?gesture=normal") {
                               transform = normal()
                            }

                            "modal" -> route("target?gesture=modal") {
                               transform = modal()
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