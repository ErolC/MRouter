package com.erolc.lifecycle

import androidx.compose.ui.uikit.ComposeUIViewControllerDelegate
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationWillEnterForegroundNotification
import platform.objc.sel_registerName


/**
 * @author erolc
 * @since 2024/2/5 09:18
 */
@OptIn(ExperimentalForeignApi::class)
class UIViewControllerDelegate(
    private val backgroundDelegate: UIApplicationBackgroundDelegate,
    private val lifecycleDelegate: LifecycleDelegate
) : ComposeUIViewControllerDelegate {


    override fun viewDidLoad() {
        super.viewDidLoad()
        lifecycleDelegate.onCreate()
        NSNotificationCenter.defaultCenter.addObserver(
            backgroundDelegate,
            sel_registerName("foreground"),
            UIApplicationWillEnterForegroundNotification,
            null
        )

        NSNotificationCenter.defaultCenter.addObserver(
            backgroundDelegate,
            sel_registerName("background"),
            UIApplicationDidEnterBackgroundNotification,
            null
        )
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        //onStart
    }

    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)
        lifecycleDelegate.onResume()
    }


    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        lifecycleDelegate.onPause()
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)
        lifecycleDelegate.onDestroy()
        NSNotificationCenter.defaultCenter.removeObserver(backgroundDelegate)
    }
}
