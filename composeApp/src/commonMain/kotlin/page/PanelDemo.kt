package page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.zIndex
import com.erolc.mrouter.PanelHost
import com.erolc.mrouter.utils.Page
import com.erolc.mrouter.utils.loge

@Composable
fun PanelDemo() = Page {
    Row {
        Column(
            Modifier.background(Color.Blue).fillMaxSize().weight(1f).zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                route("first") {
                    panel("local", false)
                }
            }) {
                Text("first")
            }
            Button(onClick = {
                route("second") {
                    panel("local", true)
                }
            }) {
                Text("second")

            }
        }
        PanelHost(modifier = Modifier.weight(2f), onPanelChange = {
            loge("tag", "isAttach:$it")
        })
    }
}
