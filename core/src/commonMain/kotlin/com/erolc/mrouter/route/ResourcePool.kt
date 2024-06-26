package com.erolc.mrouter.route

import androidx.core.bundle.Bundle
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.model.Address

/**
 * 资源池，用于存放地址资源以及平台资源
 */
internal object ResourcePool {
    private val normal: MutableList<Address> = mutableListOf()
    private val dynamic: MutableList<DynamicWrap> = mutableListOf()
    private val platformRes = mutableMapOf<String, Any>()

    private fun addAddress(address: Address) {
        val splits = address.path.split("?")
        if (FILL_IN_PATTERN.find(splits.first()) != null) {
            dynamic.add(DynamicWrap(address))
        } else
            normal.add(address)
    }

    /**
     * 添加所有地址和资源
     */
    fun addAll(addresses: List<Address>, platformRes: Map<String, Any>) {
        addresses.forEach { addAddress(it) }
        this.platformRes.putAll(platformRes)
    }

    /**
     * 添加平台资源
     */
    fun addPlatformRes(pair: Pair<String, Any>) {
        platformRes[pair.first] = pair.second
    }

    /**
     * 是否没有任何地址
     */
    fun isEmpty() = normal.isEmpty() && dynamic.isEmpty()

    /**
     * 获取平台资源
     */
    fun getPlatformRes(): Map<String, Any> = platformRes

    /**
     * 根据[route]寻找对应的地址
     */
    fun findAddress(route: Route): Pair<Address, Route>? {
        return normal.find { it.match(route.address) }?.let { it to route }
            ?: findDynamicAddress(route)
    }

    private fun findDynamicAddress(route: Route): Pair<Address, Route>? {
        return dynamic.find { it.match(route.address) }?.let {
            it.address to route.copy(args = route.args.apply { putAll(it.getArgs(route.address)) })
        }
    }


    private data class DynamicWrap(var address: Address) {
        private var matches = address.path
        private var keyPath = address.path

        init {
            matches = matches.map { if (PATTERN_CHAR.contains(it)) "\\$it" else it }
                .fold("") { acc, comparable -> acc + comparable }

            FILL_IN_PATTERN.findAll(address.path).forEach {
                matches = matches.replace(it.value, KEY_PATTERN.pattern)
                val key = KEY_PATTERN.find(it.value)?.value ?: it.value
                keyPath = keyPath.replace(it.value, key)
            }
            address = address.copy(matchKey = matches)
        }

        fun match(source: String) = matches.toRegex().matches(source)

        fun getArgs(source: String): Bundle {
            return keyPath.split("/").zip(source.split("/"))
                .filter { (key, value) -> key != value }.toBundle()
        }

    }


    private const val PATTERN_CHAR = ".+*?^$()\\[]"

    private val FILL_IN_PATTERN get() = Regex("\\{[^/]+\\}")
    private val KEY_PATTERN get() =  Regex("\\w+")
}