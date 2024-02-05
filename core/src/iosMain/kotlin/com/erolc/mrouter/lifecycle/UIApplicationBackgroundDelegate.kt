package com.erolc.mrouter.lifecycle


/**
 * @author erolc
 * @since 2024/2/5 09:20
 * 用于监控app是否切换到后台，由于sel_registerName无法在module里起作用，只能在直接接触的module里实现并传入：
 * ```
 * object MyUIApplicationBackgroundDelegate :
 *     UIApplicationBackgroundDelegate by UIApplicationBackgroundDelegateImpl
 *
 * fun MainViewController() = MRouterUIViewController(MyUIApplicationBackgroundDelegate) {
 *     App()
 * }
 * ```
 */
@SinceKotlin("1.0")
interface UIApplicationBackgroundDelegate {
    var lifecycleDelegate: LifecycleDelegate
    fun foreground()
    fun background()
}

object UIApplicationBackgroundDelegateImpl : UIApplicationBackgroundDelegate {
    override var lifecycleDelegate = LifecycleDelegate()
    override fun foreground() {
        lifecycleDelegate.onResume()
    }

    override fun background() {
        lifecycleDelegate.onPause()
    }

}