package com.erolc.lifecycle

import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import java.awt.event.WindowListener

object WindowLifecycleListener : WindowListener, WindowFocusListener {
    private val delegate get() = LifecycleDelegate.lifecycleDelegate
    override fun windowOpened(e: WindowEvent?) {}

    override fun windowClosing(e: WindowEvent?) {}

    override fun windowClosed(e: WindowEvent?) {
        delegate.onDestroy()
    }

    override fun windowIconified(e: WindowEvent?) {
        delegate.onStop()
    }

    override fun windowDeiconified(e: WindowEvent?) {
        delegate.onStart()
    }

    override fun windowActivated(e: WindowEvent?) {
//        delegate.onResume()
    }

    override fun windowDeactivated(e: WindowEvent?) {
//        delegate.onPause()
    }

    override fun windowGainedFocus(e: WindowEvent?) {
        delegate.onResume()

    }

    override fun windowLostFocus(e: WindowEvent?) {
        delegate.onPause()
    }
}