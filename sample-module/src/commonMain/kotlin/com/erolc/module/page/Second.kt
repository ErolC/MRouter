package com.erolc.module.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.erolc.mrouter.scope.LifecycleObserver
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.utils.Page
import com.erolc.mrouter.platform.loge

@Composable

fun Second() = Page {
    LifecycleObserver { _, event ->
        loge("tag", "second - $event")
    }
    val args = rememberArgs()
    Box {

        Column(modifier = Modifier.fillMaxHeight().align(Alignment.Center)) {
            Button(onClick = {
                setResult {
                    putString("back_data", "secondBackData")
                }
                backPressed()
            }) {
                Text("回退并回传数据")
            }

            val value = args.getString("value", "")
            val value1 = args.getString("value1", "")
            if (value.isNotEmpty() && value1.isNotEmpty())
                Text("这是由First页面传递而来的值：$value $value1")

        }

    }

}