package com.erolc.example

import App
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.erolc.mrouter.MRouter
import com.erolc.mrouter.route.setting
import com.erolc.mrouter.route.startActivity
import page.Home


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MRouter.registerBuilder {
            startActivity("platform",TestActivity::class)
            setting("app_home",Settings.ACTION_APPLICATION_DETAILS_SETTINGS){
                data = Uri.parse("package:com.erolc.example")
            }
        }

        setContent {
            App()
        }

    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    Home()
}

