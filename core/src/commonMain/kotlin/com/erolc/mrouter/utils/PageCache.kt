package com.erolc.mrouter.utils

import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import com.erolc.mrouter.scope.LocalPageScope

/**
 * 页面缓存类，用于记住页面中那些变量。
 */
class PageCache {
    private val values = mutableListOf<Any>()
    private var index = -1

    private fun hasNext() = values.isNotEmpty() && index + 1 < values.size
    private fun next(): Any {
        index += 1
        return values[index]
    }

    fun <T : Any> getValue(): T? {
        val value = if (hasNext()) next() as? T else null
        return value
    }

    fun <T : Any> updateValue(value: T) {
        if (index == -1 || index == values.size) {
            val temp = if (index == -1) 0 else index
            values.add(temp, value)
            index += 1
        } else
            values[index] = value
    }
}


inline fun <T : Any> PageCache.cache(invalid: Boolean, block: () -> T): T {
    return getValue<T>().let {
        if (invalid || it == null) {
            val value = block()
            updateValue(value)
            value
        } else it
    }
}

@Composable
inline fun <T : Any> rememberInPage(crossinline calculation: @DisallowComposableCalls () -> T): T {
    val scope = LocalPageScope.current
    return remember(scope) {
        scope.pageCache.cache(false, calculation)
    }
}


@Composable
inline fun <T : Any> rememberInPage(
    key1: Any?,
    crossinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    val invalid = currentComposer.changed(key1)
    return remember(key1, scope) {
        scope.pageCache.cache(invalid, calculation)
    }
}


@Composable
inline fun <T : Any> rememberInPage(
    key1: Any?, key2: Any?,
    crossinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    val invalid = currentComposer.changed(key1) or currentComposer.changed(key2)

    return remember(key1, key2, scope) {
        scope.pageCache.cache(
            invalid, calculation
        )
    }
}

@Composable
inline fun <T : Any> rememberInPage(
    key1: Any?, key2: Any?, key3: Any?,
    crossinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    val invalid = currentComposer.changed(key1) or currentComposer.changed(key2) or currentComposer.changed(key3)

    return remember(key1, key2, key3, scope) {
        scope.pageCache.cache(invalid, calculation)
    }
}


@Composable
fun <T : Any> rememberInPage(
    vararg inputs: Any?,
    calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    var invalid = false
    for (key in inputs) invalid = invalid or currentComposer.changed(key)
    return remember(inputs, scope) {
        scope.pageCache.cache(invalid, calculation)
    }
}

