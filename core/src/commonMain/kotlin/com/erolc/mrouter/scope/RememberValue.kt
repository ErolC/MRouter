package com.erolc.mrouter.scope

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.utils.PrivateCache
import com.erolc.mrouter.utils.cache
import com.erolc.mrouter.utils.rememberInPage

/**
 * 记忆在pageScope中，使得对象和页面的生命周期中保持一致。
 */
@Composable
internal fun <T : Any> rememberInWindow(key:String,init: () -> T): T {
    val scope = LocalWindowScope.current
    return remember(scope) {
        scope.pageCache.cache(key,false, PrivateCache,init)
    }
}

@Composable
fun rememberLazyListState(
    key: String,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    val value = rememberInPage(key) {
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
fun rememberLazyGirdState(key: String,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyGridState {
    val value = rememberInPage(key) {
        LazyGridState(
            initialFirstVisibleItemIndex,
            initialFirstVisibleItemScrollOffset
        )
    }
    return rememberSaveable(saver = LazyGridState.Saver) {
        value
    }
}