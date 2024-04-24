package com.erolc.mrouter.utils

import com.erolc.mrouter.route.shareelement.ShareState

/**
 * 共享元素是两个页面的两个相似元素在动画的帮助下像是一个元素似的，如此便有开始的元素和结束的元素，该方法只用于开始元素
 */
inline fun <reified T> ShareState.startTransform(currentValue: T, targetValue: T): T {
    return currentValue startTransform targetValue
}
/**
 * 共享元素是两个页面的两个相似元素在动画的帮助下像是一个元素似的，如此便有开始的元素和结束的元素，该方法只用于结束元素
 */
inline fun <reified T> ShareState.endTransform(currentValue: T, targetValue: T): T {
    return currentValue endTransform targetValue
}
/**
 * 共享元素是两个页面的两个相似元素在动画的帮助下像是一个元素似的，如此便有开始的元素和结束的元素，该方法通过[isStart]参数区分是开始元素还是结束元素
 * @param isStart 是否是开始的共享元素
 * @param startValue 开始元素的值
 * @param endValue 结束元素的值
 */
inline fun <reified T> ShareState.transform(isStart: Boolean, startValue: T, endValue: T): T {
    return if (isStart) startValue startTransform endValue else startValue endTransform endValue
}


