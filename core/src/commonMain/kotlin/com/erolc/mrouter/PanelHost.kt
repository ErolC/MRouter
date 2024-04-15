package com.erolc.mrouter

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.backstack.entry.StackEntry
import com.erolc.mrouter.route.routeBuild
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.utils.loge
import com.erolc.mrouter.utils.rememberInPage
import com.erolc.mrouter.window.WindowHeightSize
import com.erolc.mrouter.window.WindowWidthSize

/**
 * 在一个页面中可以有多个局部路由，如果需要路由到这些界面上则需要指明key。
 * 比如 key:page?var=data,即可跳转到当前页面中的key局部路由的page页面上，并传入参数data
 * @param key 局部路由的id,默认的key是[Constants.defaultLocal]，只有该局部路由可以在界面变小时（局部路由消失时）并入到主路由上。
 * @param startRoute 当直接显示时所路由的第一个页面
 * @param panelState 面板状态
 * @param onPanelChange 面板显示状态改变（是否附着在页面上）
 * @param modifier 面板的Modified
 */
@Composable
fun PanelHost(
    key: String = Constants.defaultLocal,
    startRoute: String = Constants.defaultPage,
    onPanelChange: (isAttach: Boolean) -> Unit = {},
    panelState: PanelState = rememberPanelState(),
    modifier: Modifier = Modifier
) {
    val scope = LocalPageScope.current
    val router = rememberInPage("panel_router_$key", key) {
        scope.router as? PanelRouter ?: throw RuntimeException("面板内部页面不可使用面板（局部）路由")
    }
    val isAttach = panelState.shouldAttach
    rememberInPage("panel_attach", isAttach) {
        onPanelChange(isAttach)
    }

    if (isAttach) {
        val panel = rememberInPage("panel_$key", key, router) {
            router.run {
                route(routeBuild("$key:$startRoute"))
                getPanel(key)
            }
        }
        Box(modifier) {
            router.run {
                if (key == Constants.defaultLocal) showWithLocal()
                panel.Content(Modifier)
            }
        }
    } else if (key == Constants.defaultLocal)
        router.hideWithLocal()


}


/**
 * 自适应面板
 */
@Composable
fun AutoPanel(
    panelState: PanelState = rememberPanelState(),
    onPanelChange: (isAttach: Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isAttach = panelState.shouldAttach
    remember(isAttach) {
        onPanelChange(isAttach)
    }
    if (panelState.shouldAttach)
        Box(modifier) {
            content()
        }
}

/**
 *
 * @param windowWidthSize 当界面宽度大于这个尺寸级别时才显示，如果为空，则一直显示
 * @param windowHeightSize 当界面高度大于这个尺寸级别时才显示，如果为空，则一直显示
 * 当其中一个不为空时，将以不为空的为主，当两个都不为空时，只需满足一个条件即可显示
 */
@Composable
fun rememberPanelState(
    windowWidthSize: WindowWidthSize? = WindowWidthSize.Compact,
    windowHeightSize: WindowHeightSize? = null
): PanelState {
    return rememberInPage("panel_state", windowHeightSize, windowWidthSize) {
        PanelState(windowWidthSize, windowHeightSize)
    }
}

data class PanelState(
    val windowWidthSize: WindowWidthSize? = null,
    val windowHeightSize: WindowHeightSize? = null
) {

    val shouldAttach: Boolean
        @Composable get() {
            val windowScope = LocalWindowScope.current
            val windowSize by remember { windowScope.windowSize }
            return (windowWidthSize == null && windowHeightSize == null) ||
                    (windowWidthSize != null && windowSize.width.value > windowWidthSize.value) ||
                    (windowHeightSize != null && windowSize.height.value > windowHeightSize.value)
        }
}


@Composable
internal fun LocalPanelHost(key: String = Constants.defaultLocal) {
    val scope = LocalPageScope.current
    val router = rememberInPage("panel_router_$key", key) {
        scope.router as? PanelRouter ?: throw RuntimeException("面板内部页面不可使用面板（局部）路由")
    }

    val panel = rememberInPage("panel_$key", key, router) {
        router.getPanel(key)
    }
    Box {
        router.run { panel.Content(Modifier) }
    }
}

internal fun StackEntry.isLocalPanelEntry(): Boolean {
    return address.path == Constants.defaultPrivateLocal
}