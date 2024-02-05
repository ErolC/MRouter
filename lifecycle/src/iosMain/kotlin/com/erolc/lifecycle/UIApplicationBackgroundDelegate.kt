package com.erolc.lifecycle


/**
 * @author erolc
 * @since 2024/2/5 09:20
 * 用于监控app的前后台切换，由于sel_registerName无法在间接接触的module里起作用，只能在直接接触的module里实现并传入：
 * ```
 * object SwitchBackgroundDelegate :
 *     UIApplicationBackgroundDelegate by UIApplicationBackgroundDelegateImpl
 *
 * fun MainViewController() = MRouterUIViewController(SwitchBackgroundDelegate) {
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
    override var lifecycleDelegate = LifecycleDelegate.lifecycleDelegate
    override fun foreground() {
        lifecycleDelegate.onResume()
    }

    override fun background() {
        lifecycleDelegate.onPause()
    }

}