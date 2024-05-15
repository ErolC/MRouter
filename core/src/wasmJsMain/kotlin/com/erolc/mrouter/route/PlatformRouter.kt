package com.erolc.mrouter.route

import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.model.WebRoute
import com.erolc.mrouter.register.RegisterBuilder

/**
 * @param address 地址
 * @param url window.open的url
 * @param target window.open的target
 * @param features windown.open的features
 */
fun RegisterBuilder.platformRoute(
    address: String,
    url: String,
    target: String = "", features: String = "",
) = registerPlatformResource(address, PlatformRoute(WebRoute(url, target, features)))
