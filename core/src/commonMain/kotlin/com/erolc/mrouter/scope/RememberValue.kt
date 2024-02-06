package com.erolc.mrouter.scope

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.erolc.mrouter.backstack.LocalWindowScope

/**
 * 记忆在pageScope中，使得对象和页面的生命周期中保持一致。
 */
@Composable
fun <T : Any> rememberInPage(
    vararg inputs: Any?,
    key: String,
    init: () -> T
): T {
    val scope = LocalPageScope.current
    return remember(inputs, scope) {
        scope.getValue(key) ?: run {
            val value = init()
            scope.saveValue(key, value)
            value
        }
    }
}

@Composable
internal fun <T : Any> rememberInWindow(
    vararg inputs: Any?,
    key: String,
    init: () -> T
): T {
    val scope = LocalWindowScope.current

    return remember(inputs, scope) {
        scope.getValue(key) ?: run {
            val value = init()
            scope.saveValue(key, value)
            value
        }
    }
}

private val MaxSupportedRadix = 36


@Composable
fun rememberLazyListState(
    key: String = "defaultLazyListState",
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    val value = rememberInPage(key = key) {
        LazyListState(
            initialFirstVisibleItemIndex,
            initialFirstVisibleItemScrollOffset
        )
    }
    return rememberSaveable(saver = LazyListState.Saver) {
        value
    }
}


@Composable
fun rememberLazyGirdState(
    key: String = "defaultLazyGirdState",
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyGridState {
    val value = rememberInPage(key = key) {
        LazyGridState(
            initialFirstVisibleItemIndex,
            initialFirstVisibleItemScrollOffset
        )
    }
    return rememberSaveable(saver = LazyGridState.Saver) {
        value
    }
}