package com.erolc.mrouter.platform

actual fun log(tag: String, msg: String) {
    println("$tag:$msg")
}
actual fun logi(tag: String, msg: String) {
    println("$tag:$msg")
}
actual fun loge(tag: String, msg: String) {
    println("$tag:$msg")
}