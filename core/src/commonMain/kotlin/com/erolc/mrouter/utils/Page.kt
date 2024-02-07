package com.erolc.mrouter.utils

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.erolc.mrouter.backstack.StackEntry

fun List<StackEntry>.isBack(current: List<StackEntry>): Boolean {
    if (isEmpty()) return false
    if (current.size < this.size) return true
    else
        return current[1].address.path == first().address.path
}

fun List<StackEntry>.equalsWith(current: List<StackEntry>): Boolean {
    if (isEmpty() or current.isEmpty()) return true
    return first().address.path == current.first().address.path
}

/**
 *
 */
@Composable
fun Transition<List<StackEntry>>.PageShow() {
    val oldEntries = currentState
    val newEntries = targetState
    val isBack = remember(newEntries) {
        oldEntries.isBack(newEntries)
    }
    val resumePage = remember(newEntries) {
        newEntries.lastOrNull()
    }

    val bottomPage = remember(newEntries) {
        if (newEntries.size == 1) emptyList() else
            newEntries.take(newEntries.size - 1)
    }

}