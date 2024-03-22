package com.erolc.mrouter.route

import com.erolc.mrouter.Constants
import com.erolc.mrouter.dialog.DialogBuilder
import com.erolc.mrouter.dialog.DialogOptions
import com.erolc.mrouter.model.Route
import com.erolc.mrouter.utils.isMobile
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.route.transform.*
import com.erolc.mrouter.window.WindowOptionsBuilder

/**
 * 构建路由的方法
 */
fun routeBuild(route: String, optionsBuilder: RouteBuilder.() -> Unit = {}): Route =
    RouteBuilder().apply(optionsBuilder).build(route)

/**
 * 路由构建类，用于构建路由到下一个页面所需的一些数据：参数，回调等。
 */
class RouteBuilder {
    private var onResult: (Args) -> Unit = {}
    private var windowOptions: WindowOptions = WindowOptions(Constants.defaultWindow, "")

    private val args = emptyArgs

    var flag:RouteFlag = NormalFlag

    var transform: Transform = none()

    fun transform(body: TransformBuilder.() -> Unit) {
        transform = buildTransform(body)
    }

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
     * 在桌面端以外的场景，[window]是不起作用的
     * @param id 窗口的id
     * @param builder 窗口的设置
     */
    fun window(
        id: String = Constants.defaultWindow,
        title: String,
        builder: WindowOptionsBuilder.() -> Unit = {}
    ) {
        windowOptions = WindowOptionsBuilder().apply(builder)
            .build(if (isMobile) Constants.defaultWindow else id, title)
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
        return Route(
            route,
            address,
            flag,
            windowOptions,
            args,
            onResult,
            key,
            transform
        )
    }


    private fun getPaths(path: String): Pair<String?, String> {
        var key: String? = null
        var address = path
        if (path.contains(":")) {
            val paths = path.split(":")
            key = paths[0]
            address = paths[1]
        }
        return key to address
    }


}