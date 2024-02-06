package com.erolc.mrouter.model

/**
 * Describes how the window is placed on the screen.
 */
enum class WindowState {
    /**
     * Window don't occupy the all available space and can be moved and resized by the user.
     */
    Floating,

    /**
     * The window is maximized and occupies all available space on the screen excluding
     * the space that is occupied by the screen insets (taskbar/dock and top-level application menu
     * on macOs).
     */
    Maximized,

    /**
     * The window is in fullscreen mode and occupies all available space of the screen,
     * including the space that is occupied by the screen insets (taskbar/dock and top-level
     * application menu on macOs).
     */
    Fullscreen
}