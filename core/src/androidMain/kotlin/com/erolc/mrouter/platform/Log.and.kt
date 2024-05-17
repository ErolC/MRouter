package com.erolc.mrouter.platform

import android.util.Log

actual fun log(tag: String, msg: String) {
    Log.d(tag, msg)
}

actual fun logi(tag: String, msg: String) {
    Log.i(tag, msg)
}

actual fun loge(tag: String, msg: String) {
    Log.e(tag, msg)
}