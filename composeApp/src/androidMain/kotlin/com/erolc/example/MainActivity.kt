package com.erolc.example

import App
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.coroutineScope

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            App()
            AnimatedScreen()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}



@Composable
fun AnimatedScreen() {
    var offsetX by remember { mutableStateOf(10f) }
    val transition = updateTransition(targetState = offsetX, label = "Animation Transition")

    val alpha by transition.animateFloat(
        label = "Alpha Transition",
        transitionSpec = {
            tween(durationMillis = 500)
        }
    ) { offsetX ->
        offsetX/100
    }

    val offsetXConstraint = if (offsetX == 0f) 0f else 200f


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(alpha)
                .background(Color.Red)

        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .offset(x = offsetX.dp, y = 0.dp)
                .background(Color.Blue)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        val newOffsetX = offsetX + dragAmount.x

                        if (newOffsetX in 0f .. offsetXConstraint) {
                            offsetX = newOffsetX
                        }
                    }
                }
        )
    }
}
