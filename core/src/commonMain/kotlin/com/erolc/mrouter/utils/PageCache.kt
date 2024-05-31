package com.erolc.mrouter.utils

import androidx.compose.runtime.*
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.scope.LocalPageScope


/**
 * 页面缓存类，用于记住页面中那些变量。
 * 使用该类构造的remember方法：[rememberPrivateInPage],[rememberInPage]的生命周期长度将处在[remember]和[rememberSaveable]之间。
 */
class PageCache {
    private val values = mutableMapOf<String, Any>()

    private fun has(key: String) = values.containsKey(key)

    internal fun getValue(key: String): Any? {
        return if (has(key)) values[key] else null
    }

    internal fun updateValue(key: String, value: Any) {
        values[key] = value
    }

    internal fun clear() = values.clear()
}

sealed interface CacheState

internal data object PrivateCache : CacheState
data object NormalCache : CacheState


fun <T : Any> PageCache.cache(key: String, state: CacheState = NormalCache, block: () -> T): T {
    val realKey = if (state == NormalCache) "cache_$key" else key
    return getValue(realKey).let {
        if (it == null) {
            val value = block()
            updateValue(realKey, value)
            value
        } else it
    } as T
}

/**
 * 该系列方法的用法和[remember]一样，不同的是该方法所保存的变量的生命周期会比[remember]保存的要长，和当前界面的一样长
 * 需要注意的是，
 * @param key 用于指定该记住在当前页面的数据，可再次通过该key从页面缓存中提取出来
 *
 */
@Composable
inline fun <T : Any> rememberInPage(
    key: String,
    noinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    return remember(scope) {
        scope.pageCache.cache(key, block = calculation)
    }
}


@Composable
inline fun <T : Any> rememberInPage(
    key: String,
    key1: Any?,
    noinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    return remember(key1, scope) {
        scope.pageCache.cache(key, block = calculation)
    }
}


@Composable
inline fun <T : Any> rememberInPage(
    key: String,
    key1: Any?, key2: Any?,
    noinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    return remember(key1, key2, scope) {
        scope.pageCache.cache(
            key, block = calculation
        )
    }
}

@Composable
inline fun <T : Any> rememberInPage(
    key: String,
    key1: Any?, key2: Any?, key3: Any?,
    noinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    return remember(key1, key2, key3, scope) {
        scope.pageCache.cache(key, block = calculation)
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
        scope.pageCache.cache(key, block = calculation)
    }
}


@Composable
internal inline fun <T : Any> rememberPrivateInPage(
    key: String,
    noinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    return remember(scope) {
        scope.pageCache.cache(key, PrivateCache, calculation)
    }
}


@Composable
internal inline fun <T : Any> rememberPrivateInPage(
    key: String,
    key1: Any?,
    noinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    return remember(key1, scope) {
        scope.pageCache.cache(key, PrivateCache, calculation)
    }
}

@Composable
internal inline fun <T : Any> rememberPrivateInPage(
    key: String,
    key1: Any?, key2: Any?,
    noinline calculation: @DisallowComposableCalls () -> T
): T {
    val scope = LocalPageScope.current
    return remember(key1, key2, scope) {
        scope.pageCache.cache(
            key, PrivateCache, calculation
        )
    }
}

/**
 * 记忆在pageScope中，使得对象和页面的生命周期中保持一致。
 */
@Composable
internal fun <T : Any> rememberInWindow(key: String, init: () -> T): T {
    val scope = LocalWindowScope.current
    return remember(scope) {
        scope.pageCache.cache(key, PrivateCache, init)
    }
}