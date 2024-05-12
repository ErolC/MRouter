package com.erolc.mrouter.route

import androidx.core.bundle.Bundle
import androidx.core.bundle.bundleOf

typealias RouteResult = (Bundle) -> Unit

/**
 * 路由的一些标识，可以指导路由期间内部的一些变化。
 */
sealed interface RouteFlag {

    private class RouteFlagImpl() : RouteFlag {
        private val _flags = mutableListOf<RouteFlag>()
        val flags: List<RouteFlag> get() = _flags

        constructor(flag: RouteFlag, sFlag: RouteFlag) : this() {
            when {
                flag is RouteFlagImpl && sFlag is RouteFlagImpl -> {
                    _flags += flag._flags
                    _flags += sFlag._flags
                }

                flag !is RouteFlagImpl && sFlag is RouteFlagImpl -> {
                    _flags += flag
                    _flags += sFlag._flags
                }

                flag is RouteFlagImpl && sFlag !is RouteFlagImpl -> {
                    _flags += flag._flags
                    _flags += sFlag
                }

                else -> {
                    _flags += flag
                    _flags += sFlag
                }
            }
        }
    }

    infix fun or(flag: RouteFlag): RouteFlag {
        return RouteFlagImpl(this, flag)
    }

    operator fun plus(flag: RouteFlag): RouteFlag {
        return RouteFlagImpl(this, flag)
    }

    fun decode(): List<RouteFlag> {
        return if (this is RouteFlagImpl) flags else listOf(this)
    }
}

/**
 * 回退栈类型的flag
 */
sealed class StackFlag : RouteFlag

/**
 * 正常
 */
data object NormalFlag : RouteFlag

/**
 * 清空当前栈
 */
data object ClearTaskFlag : StackFlag()

/**
 * 更换当前回退栈中存在的页面。
 */
internal data object ReplaceFlag : StackFlag()



/**
 * 是否是基础数据类型
 */
fun isBasic(value: Any): Boolean {
    return when (value) {
        is String -> true
        is Int -> true
        is Boolean -> true
        is Short -> true
        is Long -> true
        is Byte -> true
        is Char -> true
        is Double -> true
        is Float -> true
        is UInt -> true
        is ULong -> true
        is UShort -> true
        is UByte -> true
        else -> false
    }
}

/**
 * 转换数据，如果不是基础数据类型或无法转换则返回null
 */
inline fun <reified T> getBasicData(value: Any): T? {
    return if (isBasic(value))
        value as? T ?: when (T::class) {
            Int::class -> when (value) {
                is String -> value.toIntOrNull() ?: 0
                is Boolean -> if (value) 1 else 0
                is Number -> value.toInt()
                is Char -> value.digitToInt()
                else -> 0
            }

            String::class -> value.toString()
            Boolean::class -> when (value) {
                is String -> value.toBooleanStrictOrNull() ?: false
                is Int -> value >= 1
                is Short -> value > 0
                is Long -> value > 0
                is Byte -> value > 0
                is Char -> value.code > 0
                is Double -> value > 0
                is Float -> value > 0
                else -> false
            }

            Short::class -> when (value) {
                is Number -> value.toShort()
                is String -> value.toShortOrNull() ?: 0
                is Boolean -> (if (value) 1 else 0).toShort()
                else -> 0
            }

            Long::class -> when (value) {
                is Number -> value.toLong()
                is String -> value.toLongOrNull() ?: 0L
                is Boolean -> if (value) 1L else 0L
                is Char -> value.code.toLong()
                else -> 0L
            }

            Byte::class -> when (value) {
                is Number -> value.toByte()
                is String -> value.toByteOrNull() ?: 0
                is Boolean -> (if (value) 1 else 0).toByte()
                is Char -> value.code.toByte()
                else -> 0
            }

            Char::class -> when (value) {
                is Number -> value.toInt().toChar()
                is Boolean -> if (value) 1.digitToChar() else 0.digitToChar()
                else -> '0'
            }

            Double::class -> when (value) {
                is Number -> value.toDouble()
                is String -> value.toDoubleOrNull() ?: 0.0
                is Boolean -> if (value) 1.0 else 0.0
                is Char -> value.code.toDouble()
                else -> 0.0
            }

            Float::class -> when (value) {
                is Number -> value.toFloat()
                is String -> value.toFloatOrNull() ?: 0f
                is Boolean -> if (value) 1f else 0f
                is Char -> value.code.toFloat()
                else -> null
            }
            //        is UByte -> true
            UInt::class -> when (value) {
                is Number -> value.toInt().toUInt()
                is String -> value.toUIntOrNull() ?: 0
                is Boolean -> (if (value) 1 else 0).toUInt()
                is Char -> value.code.toUInt()
                else -> 0
            }

            ULong::class -> when (value) {
                is Number -> value.toLong().toULong()
                is String -> value.toULongOrNull() ?: 0L
                is Boolean -> (if (value) 1 else 0).toULong()
                is Char -> value.code.toULong()
                else -> 0L
            }

            UShort::class -> when (value) {
                is Number -> value.toShort().toUShort()
                is String -> value.toUShortOrNull() ?: 0
                is Boolean -> (if (value) 1 else 0).toUShort()
                is Char -> value.code.toUShort()
                else -> 0
            }

            UByte::class -> when (value) {
                is Number -> value.toByte().toUByte()
                is String -> value.toUByteOrNull() ?: 0
                is Boolean -> (if (value) 1 else 0).toUByte()
                is Char -> value.code.toUByte()
                else -> 0
            }

            else -> null
        } as? T
    else null
}

fun List<Pair<String,String>>.toBundle() = bundleOf(*toTypedArray())