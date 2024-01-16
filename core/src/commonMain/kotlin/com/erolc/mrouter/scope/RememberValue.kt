package com.erolc.mrouter.scope

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * 记忆在pageScope中，使得对象和页面的生命周期中保持一致。
 */
@Composable
fun <T : Any> rememberInPage(
    vararg inputs: Any?,
    key: String? = null,
    init: () -> T
): T {
    val scope = LocalPageScope.current

    val finalKey = if (key.isNullOrEmpty()) {
        currentCompositeKeyHash.toString(MaxSupportedRadix)
    } else key

    return remember(inputs, scope, finalKey) {
        scope.getValue(finalKey) ?: run {
            val value = init()
            scope.saveValue(finalKey, value)
            value
        }
    }
}

private val MaxSupportedRadix = 36


@Composable
fun rememberLazyListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    val value = rememberInPage {
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
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyGridState {
    val value = rememberInPage {
        LazyGridState(
            initialFirstVisibleItemIndex,
            initialFirstVisibleItemScrollOffset
        )
    }
    return rememberSaveable(saver = LazyGridState.Saver) {
        value
    }
}