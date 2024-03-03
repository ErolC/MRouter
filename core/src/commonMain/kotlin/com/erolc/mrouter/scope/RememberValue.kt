package com.erolc.mrouter.scope

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.utils.cache
import com.erolc.mrouter.utils.rememberInPage

/**
 * 记忆在pageScope中，使得对象和页面的生命周期中保持一致。
 */

// todo 需要更细致的测试一下pageRemember
@Composable
internal fun <T : Any> rememberInWindow(init: () -> T): T {
    val scope = LocalWindowScope.current
    return remember(scope) {
        scope.pageCache.cache(false, init)
    }
}

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