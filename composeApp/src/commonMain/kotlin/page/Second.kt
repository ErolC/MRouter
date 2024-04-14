package page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.erolc.mrouter.route.Arg
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.utils.Page

@Composable

fun Second() = Page {
    val args = rememberArgs()
    Box {

        Column(modifier = Modifier.fillMaxHeight().align(Alignment.Center)) {
            Button(onClick = {
                if (args.getData<Boolean>("return")) {
                    setResult(Arg("back_data", "secondBackData"))
                }
                backPressed()
            }) {
                Text("回退${if (args.getData<Boolean>("return")) "并回传数据" else ""}")
            }

            val value = args.getData<String>("value")
            val value1 = args.getData<String>("value1")
            if (value.isNotEmpty() && value1.isNotEmpty())
                Text("这是由First页面传递而来的值：$value $value1")

        }

    }

}