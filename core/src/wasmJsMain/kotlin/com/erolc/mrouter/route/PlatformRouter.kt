package com.erolc.mrouter.route

import com.erolc.mrouter.model.PlatformRoute
import com.erolc.mrouter.model.WebRoute
import com.erolc.mrouter.register.Register

/**
 * @param address 地址
 * @param url window.open的url
 * @param target window.open的target
 * @param features windown.open的features
 */
fun Register.platformRoute(
    address: String,
    url: String,
    target: String = "", features: String = "",
) = addPlatformResource(address, PlatformRoute(WebRoute(url, target, features)))
