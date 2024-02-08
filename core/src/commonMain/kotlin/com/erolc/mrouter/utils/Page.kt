package com.erolc.mrouter.utils

import androidx.compose.animation.*
import androidx.compose.animation.core.Transition
import androidx.compose.ui.Modifier
import com.erolc.mrouter.backstack.PageEntry
import com.erolc.mrouter.backstack.StackEntry
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.animateValueAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.*
import com.erolc.mrouter.scope.rememberInPage


fun List<StackEntry>.isBack(current: List<StackEntry>): Boolean {
    if (isEmpty()) return false
    return if (current.size < this.size) true
    else {
        val last = current.last()
        val first = first()
        last.address.path == first.address.path
    }
}

fun List<StackEntry>.equalsWith(current: List<StackEntry>): Boolean {
    return firstOrNull()?.address?.path == current.firstOrNull()?.address?.path && size == current.size
}

enum class PageState {
    //open:init - opening - init
    //  false true   - false A - B
    //close:int - closing - init
    // false  - true false B - A

    Closing,
    Init,
    Opening,
}

/**
 *
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Transition<List<StackEntry>>.PageShow(modifier: Modifier = Modifier) {
    val pageState = remember(targetState, currentState) {
        if (currentState.equalsWith(targetState)) PageState.Init
        else {
            if (currentState.isBack(targetState)) PageState.Closing else PageState.Opening
        }
    }

    var animEntry by remember {
        mutableStateOf<PageEntry?>(null)
    }
    val isVisibility by remember { mutableStateOf(false) }


    if (pageState == PageState.Init) {
        //这里是静态节点，在这里做一些切换页面之后的赋值操作。
    }


    val bottomPage = remember(targetState) {
        if (pageState == PageState.Closing) targetState.firstOrNull() as? PageEntry else currentState.firstOrNull() as? PageEntry
    }

    val resumePage = remember(targetState) {
        if (pageState == PageState.Opening) targetState.lastOrNull() as? PageEntry else currentState.lastOrNull() as? PageEntry
    }

    bottomPage?.Content(modifier)

    //这个仅仅使用来将界面进入和退出用的动画，这里不会存在一个长期的entry，
    AnimatedVisibility({ isVisibility }) { animEntry?.Content(modifier) }
    resumePage?.Content(modifier)

}