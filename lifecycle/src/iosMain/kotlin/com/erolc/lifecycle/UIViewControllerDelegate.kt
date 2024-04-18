package com.erolc.lifecycle

import androidx.compose.ui.uikit.ComposeUIViewControllerDelegate


/**
 * @author erolc
 * @since 2024/2/5 09:18
 */
class UIViewControllerDelegate(
    private val lifecycleDelegate: LifecycleDelegate
) : ComposeUIViewControllerDelegate {

    private val applicationStateListener = ApplicationStateListener { isForeground ->
        if (isForeground) lifecycleDelegate.onResume() else lifecycleDelegate.onPause()
    }

    override fun viewDidLoad() {
        super.viewDidLoad()
        lifecycleDelegate.onCreate()
    }

    override fun viewWillAppear(animated: Boolean) {
        super.viewWillAppear(animated)
        lifecycleDelegate.onStart()
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
        lifecycleDelegate.onStop()
        lifecycleDelegate.onDestroy()
        applicationStateListener.dispose()
    }
}
