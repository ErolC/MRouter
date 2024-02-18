package com.erolc.mrouter.utils

import androidx.compose.animation.*
import androidx.compose.animation.core.Transition
import androidx.compose.ui.Modifier
import com.erolc.mrouter.backstack.PageEntry
import com.erolc.mrouter.backstack.StackEntry
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.*


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

fun List<StackEntry>.isInit(current: List<StackEntry>): Boolean {
    return equalsWith(current)
}

fun List<StackEntry>.isEmpty(current: List<StackEntry>): Boolean {
    return isEmpty() || current.isEmpty()
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


@Composable
fun Transition<List<StackEntry>>.PageTransition(modifier: Modifier = Modifier) {
    val pageState = remember(targetState, currentState) {
        if (currentState.isInit(targetState)) PageState.Init
        else {
            if (currentState.isBack(targetState)) PageState.Closing else PageState.Opening
        }
    }
    val state = when(pageState){
        PageState.Init -> 0
        PageState.Opening -> 100
        PageState.Closing -> -100
    }
    //当从100/-100变为0的阶段为重置，将不再执行tran
    val tran = updateTransition(state)
    tran.PageTransition({
        targetState.firstOrNull()?.Content(modifier)
    },{
        targetState.lastOrNull()?.Content(modifier)
    })
}

/**
 * 这里将不在使用PageState，而是使用数字0～100为打开，0～-100为关闭。
 */
@Composable
fun Transition<Int>.PageTransition(currentPage:@Composable ()->Unit,targetPage:@Composable ()->Unit) {
    //当前界面，是当前显示的界面
    currentPage()
    //目标界面，如果pageState是opening，那么它将是未打开的界面，如果是closing，那么它将是在当前界面下的前页面。
    targetPage()
}


/**
 *
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Transition<List<StackEntry>>.PageShow(modifier: Modifier = Modifier) {
    val pageState = remember(targetState, currentState) {
        if (currentState.isInit(targetState)) PageState.Init
        else {
            if (currentState.isBack(targetState)) PageState.Closing else PageState.Opening
        }
    }
    loge("tag", " -------------$pageState ${targetState} ${currentState}")

    //播放进入动画时临时承载的entry
    var entryEntry by remember(targetState) {
        mutableStateOf<PageEntry?>(targetState.lastOrNull() as? PageEntry)
    }
    //播放退出动画是临时承载的entry
    var exitEntry by remember {
        mutableStateOf<PageEntry?>(null)
    }
    //进入动画的标志：true进入，默认是false，在完成进入动画之后将变成false
    var isEnterVisibility by remember { mutableStateOf(true) }
    //退出动画的标志，false退出，默认是true，在完成退出动画之后将变成true
    var isExitVisibility by remember { mutableStateOf(true) }

    //底部的页面。在完成进入动画时，resume将替换该位置
    var bottomPage by remember {
        mutableStateOf<PageEntry?>(null)
    }
    //当前页面。在进入动画完成时，进入动画的entry将替换该位置。
    var resumePage by remember {
        mutableStateOf<PageEntry?>(null)
    }
    when (pageState) {
        PageState.Init -> {
            if (isEnterVisibility) {
                entryEntry?.also { resumePage = it }
                entryEntry = null
                isEnterVisibility = false
            }

            if (!isExitVisibility) {
                bottomPage = targetState.firstOrNull() as? PageEntry
                exitEntry = null
                isExitVisibility = true
            }
        }

        PageState.Opening -> {
            bottomPage = resumePage
            entryEntry = targetState.lastOrNull() as? PageEntry
            isEnterVisibility = true
        }

        PageState.Closing -> {
            exitEntry = resumePage
            resumePage = bottomPage
            isExitVisibility = false
        }
    }

    bottomPage?.Content(modifier)
    resumePage?.Content(modifier)

    //用于播放退出动画
    AnimatedVisibility({ isExitVisibility }) { exitEntry?.Content(modifier) }
    //用于播放进入动画
    AnimatedVisibility({ isEnterVisibility }) { entryEntry?.Content(modifier) }


}