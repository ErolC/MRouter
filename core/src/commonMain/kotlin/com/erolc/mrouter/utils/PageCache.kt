package com.erolc.mrouter.utils

import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot
import com.erolc.mrouter.scope.LocalPageScope

/**
 * 页面缓存类，用于记住页面中那些变量。
 */
class PageCache {
    private val values = mutableMapOf<String, Any>()

    private fun has(key: String) = values.containsKey(key)

    fun getValue(key: String): Any? {
        return if (has(key)) values[key] else null
    }

    fun updateValue(key: String, value: Any) {
        values[key] = value
    }
}


inline fun <T : Any> PageCache.cache(key: String, invalid: Boolean, block: () -> T): T {
    return getValue(key).let {
        if (invalid || it == null) {
            val value = block()
            updateValue(key, value)
            value
        } else it
    } as T
}

/**
 * 该系列方法的用法和[remember]一样，不同的是该方法所保存的变量的生命周期会比[remember]保存的要长，和当前界面的一样长.
 *
 */
@Composable
inline fun <T : Any> rememberInPage(key: String, crossinline calculation: @DisallowComposableCalls () -> T): T {
    val scope = LocalPageScope.current
    return remember(scope) {
        scope.pageCache.cache(key, false, calculation)
    }
}


@Composable
inline fun <T : Any> rememberInPage(
    key: String,
    key1: Any?,
    crossinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    val invalid = currentComposer.changed(key1)
    return remember(key1, scope) {
        scope.pageCache.cache(key, invalid, calculation)
    }
}


@Composable
inline fun <T : Any> rememberInPage(
    key: String,
    key1: Any?, key2: Any?,
    crossinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    val invalid = currentComposer.changed(key1) or currentComposer.changed(key2)

    return remember(key1, key2, scope) {
        scope.pageCache.cache(
            key,
            invalid, calculation
        )
    }
}

@Composable
inline fun <T : Any> rememberInPage(
    key: String,
    key1: Any?, key2: Any?, key3: Any?,
    crossinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    val invalid = currentComposer.changed(key1) or currentComposer.changed(key2) or currentComposer.changed(key3)

    return remember(key1, key2, key3, scope) {
        scope.pageCache.cache(key, invalid, calculation)
    }
}


@Composable
fun <T : Any> rememberInPage(
    key: String,
    vararg inputs: Any?,
    calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    var invalid = false
    for (temp in inputs) invalid = invalid or currentComposer.changed(temp)
    return remember(inputs, scope) {
        scope.pageCache.cache(key, invalid, calculation)
    }
}

