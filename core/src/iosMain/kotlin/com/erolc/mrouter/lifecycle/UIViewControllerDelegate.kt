package com.erolc.mrouter.lifecycle

import androidx.compose.ui.uikit.ComposeUIViewControllerDelegate
import com.erolc.mrouter.utils.logi
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
) :
    ComposeUIViewControllerDelegate {

    override fun viewDidAppear(animated: Boolean) {
        super.viewDidAppear(animated)
        logi("tag", "viewDidAppear")
    }

    override fun viewDidDisappear(animated: Boolean) {
        super.viewDidDisappear(animated)
        logi("tag", "viewDidDisappear")
        NSNotificationCenter.defaultCenter.removeObserver(backgroundDelegate)
        lifecycleDelegate.onDestroy()
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        logi("tag", "viewDidLoad")
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
        logi("tag", "viewWillAppear")

    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        logi("tag", "viewWillDisappear")
    }
}
