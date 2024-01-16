package com.erolc.mrouter.route

import com.erolc.mrouter.Constants
import com.erolc.mrouter.dialog.DialogBuilder
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.utils.isMobile
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.window.WindowOptionsBuilder


fun routeBuild(route: String, optionsBuilder: RouteBuilder.() -> Unit = {}): Route =
    RouteBuilder().apply(optionsBuilder).build(route)

class RouteBuilder {
    private var onResult: (Args) -> Unit = {}
    private var windowOptions: WindowOptions = WindowOptions(Constants.defaultWindow, "")

    private val args = emptyArgs

    fun arg(key: String, value: Any) {
        args += (key to value).toArg()
    }

    fun args(vararg pair: Pair<String, Any>) {
        args += pair.toMap().toArgs()
    }

    fun arg(arg: Arg) {
        args += arg
    }

    fun args(args: Args) {
        this.args += args
    }

    fun onResult(body: (Args) -> Unit) {
        onResult = body

    }

    /**
     * 以[id]对应的窗口打开，如果没有则创建一个新窗口打开，
     * 需要注意1：ios和android都只有一个窗口，所以目前可以以多窗口模式运行的只有desktop
     * 需要注意2：由于目前的设计思路，窗口的关闭只能按照创建的反顺序关闭，否则会有意想不到的效果
     * @param id 窗口的id
     * @param builder 窗口的设置
     */
    fun window(id: String = Constants.defaultWindow, title: String, builder: WindowOptionsBuilder.() -> Unit = {}) {
        windowOptions = WindowOptionsBuilder().apply(builder)
            .build(if (isMobile) Constants.defaultWindow else id, title)
    }

    /**
     * 以弹框的形式打开该窗口，需要注意的是该方法和[window]方法是互斥的，且[window]方法优先级更高。
     *
     */
    fun dialog(builder: DialogBuilder.() -> Unit) {

    }

    internal fun build(route: String): Route {
        val split = route.split("?")
        val path = split[0]
        val (key, address) = getPaths(path)
        val args = emptyArgs
        if (split.size == 2) {
            args += split[1].split("&").map {
                val (aKey, value) = it.split("=")
                aKey to value
            }.filter {
                it.second.isNotEmpty()
            }.toMap().toArgs()
        }
        args += args
        return Route(route, address, windowOptions, args, onResult = onResult, layoutKey = key)
    }


    private fun getPaths(path: String): Pair<String?, String> {
        var key: String? = null
        var address = path
        if (path.contains("/")) {
            val paths = path.split("/")
            key = paths[0]
            address = paths[1]
        }
        return key to address
    }


}