package com.erolc.mrouter

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.route.routeBuild
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.route.transform.EnterState
import com.erolc.mrouter.route.transform.ResumeState
import com.erolc.mrouter.route.transform.TransformState
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.utils.rememberPrivateInPage
import com.erolc.mrouter.window.WindowHeightSize
import com.erolc.mrouter.window.WindowWidthSize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

/**
 * 在一个页面中可以有多个局部路由，如果需要路由到这些界面上则需要指明key。
 * 比如 key:page?var=data,即可跳转到当前页面中的key局部路由的page页面上，并传入参数data
 * @param key 局部路由的id,默认的key是[Constants.DEFAULT_PANEL]，只有该局部路由可以在界面变小时（局部路由消失时）并入到主路由上。
 * @param startRoute 当直接显示时所路由的第一个页面
 * @param panelState 面板状态
 * @param onPanelChange 面板显示状态改变（是否附着在页面上）
 * @param modifier 面板的Modified
 */
@Composable
fun PanelHost(
    key: String = Constants.DEFAULT_PANEL,
    startRoute: String = Constants.DEFAULT_PAGE,
    onPanelChange: (isAttach: Boolean) -> Unit = {},
    panelState: PanelState = rememberPanelState(),
    modifier: Modifier = Modifier
) {
    val scope = LocalPageScope.current
    val router = rememberPrivateInPage("panel_router_$key", key) {
        scope.router as? PanelRouter ?: throw RuntimeException("非法路由器")
    }
    val isAttach = panelState.shouldAttach
    rememberPrivateInPage("panel_attach", isAttach) {
        onPanelChange(isAttach)
    }

    if (isAttach) {
        val panel = rememberPrivateInPage("panel_$key", key, router) {
            router.run {
                route(key, routeBuild(startRoute))
                getPanel(key)
            }.apply {
                hostScope.panelState = panelState
            }
        }
        router.showPanel(key)
        panel.Content(modifier)
    } else router.hidePanel(key)


}

/**
 * 当其中一个不为空时，将以不为空的为主，当两个都不为空时，只需满足一个条件即可显示
 * @param windowWidthSize 当界面宽度大于这个尺寸级别时才显示，如果为空，则一直显示
 * @param windowHeightSize 当界面高度大于这个尺寸级别时才显示，如果为空，则一直显示
 *
 */
@Composable
fun rememberPanelState(
    windowWidthSize: WindowWidthSize? = WindowWidthSize.Compact,
    windowHeightSize: WindowHeightSize? = null
): PanelState {
    return rememberPrivateInPage("panel_state", windowHeightSize, windowWidthSize) {
        PanelState(windowWidthSize, windowHeightSize)
    }
}

data class PanelState(
    val windowWidthSize: WindowWidthSize? = null,
    val windowHeightSize: WindowHeightSize? = null,
) {

    internal val pageState: MutableStateFlow<TransformState> = MutableStateFlow(ResumeState)
    val shouldAttach: Boolean
        @Composable get() {
            val windowScope = LocalWindowScope.current
            val windowSize by remember { windowScope.windowSize }
            return (windowWidthSize == null && windowHeightSize == null) ||
                    (windowWidthSize != null && windowSize.width.value > windowWidthSize.value) ||
                    (windowHeightSize != null && windowSize.height.value > windowHeightSize.value)
        }

    @Composable
    private fun getPageStateTransition(): Transition<TransformState> {
        val state by pageState.collectAsState()
        return updateTransition(state)
    }
}