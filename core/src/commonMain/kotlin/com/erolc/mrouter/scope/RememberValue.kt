package com.erolc.mrouter.scope

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.erolc.mrouter.utils.rememberInPage


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