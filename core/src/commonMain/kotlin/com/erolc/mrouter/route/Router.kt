package com.erolc.mrouter.route

typealias RouteResult = (Args) -> Unit

/**
 * 路由的一些标识，可以指导路由期间内部的一些变化。
 */
sealed class RouteFlag(val code: Int) {


    private class RouteFlagImpl(code: Int) : RouteFlag(code) {
        private val _flags = mutableListOf<RouteFlag>()
        val flags: List<RouteFlag> get() = _flags

        constructor(flag: RouteFlag, sFlag: RouteFlag) : this(flag.code or sFlag.code) {
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
sealed class StackFlag(code: Int) : RouteFlag(code)

/**
 * 正常
 */
data object NormalFlag : RouteFlag(0b1)

/**
 * 清空当前栈
 */
data object ClearTaskFlag : StackFlag(0b10)


