package com.erolc.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.erolc.mrouter.utils.loge

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bool = intent.getBooleanExtra("return",false)
        loge("tag","bool:$bool")
        setContent {
            Test()
        }

    }
}

@Composable
fun Activity.Test() {
    Row {
        Button(onClick = {
            setResult(0, Intent().apply {
                putExtra("back_data", "data:success")
            })
            onBackPressed()
        }) {
            Text("back")
        }
    }
}