package com.erolc.module.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import com.erolc.mrouter.lifecycle.viewModel
import com.erolc.mrouter.scope.LifecycleObserver
import com.erolc.mrouter.scope.rememberArgs
import com.erolc.mrouter.utils.Page
import com.erolc.mrouter.platform.loge

class FirstViewModel : ViewModel() {
    val result = mutableStateOf("")
    val data = mutableStateOf("success")
}

@Composable
fun First() = Page {
    LifecycleObserver { _, event ->
        loge("tag", "first - $event")
    }
    val args = rememberArgs()

    val viewModel = viewModel(::FirstViewModel)

    var result by remember { viewModel.result }
    var data by remember { viewModel.data }

    Column(modifier = Modifier.fillMaxWidth().safeContentPadding()) {
        Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
            backPressed()
        }) {
            Text("回退")
        }

        Button(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
            val key = args.getString("key")
            route("sample/second?value=queryData") {
                argBuild {
                    putString("value1", data)
                }
                onResult {
                    result = it.getString("back_data", "")
                }
            }
        }) {
            Text("前往下一个页面并携带数据")
        }
        Text("这是由Second页面回传的数据：${result}")
        TextField(data,{data=it})
    }
}