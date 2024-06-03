package com.erolc.module

import com.erolc.module.page.First
import com.erolc.module.page.Second
import com.erolc.mrouter.register.Register
import com.erolc.mrouter.register.module

fun Register.sample() = module("sample") {
    page("first") {
        First()
    }
    page("second") {
        Second()
    }
}