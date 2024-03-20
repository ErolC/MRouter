package com.erolc.mrouter

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.route.routeBuild
import com.erolc.mrouter.route.router.PanelRouter
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.utils.rememberInPage
import com.erolc.mrouter.window.WindowHeightSize
import com.erolc.mrouter.window.WindowWidthSize

/**
 * 在一个页面中可以有多个局部路由，如果需要路由到这些界面上则需要指明key。
 * 比如 key:page?var=data,即可跳转到当前页面中的key局部路由的page页面上，并传入参数data
 * @param key 局部路由的id,默认的key是[Constants.defaultLocal]，只有该局部路由可以在界面变小时（局部路由消失时）并入到主路由上。
 * @param startRoute 当直接显示时所路由的第一个页面
 * @param windowWidthSize 当界面宽度大于这个尺寸级别时才显示，如果为空，则一直显示
 * @param windowHeightSize 当界面高度大于这个尺寸级别时才显示，如果为空，则一直显示
 */
@Composable
fun PanelHost(
    key: String = Constants.defaultLocal,
    startRoute: String = Constants.defaultPage,
    windowWidthSize: WindowWidthSize? = null,
    windowHeightSize: WindowHeightSize? = null,
    modifier: Modifier = Modifier
) {
    val scope = LocalPageScope.current
    val router = rememberInPage(key) {
        scope.router as? PanelRouter ?: throw RuntimeException("面板内部页面不可使用面板（局部）路由")
    }
    val windowScope = LocalWindowScope.current
    val windowSize by remember { windowScope.windowSize }
    if ((windowWidthSize == null && windowHeightSize == null) ||
        (windowWidthSize != null && windowSize.width.value > windowWidthSize.value) ||
        (windowHeightSize != null && windowSize.height.value > windowHeightSize.value)
    ) {
        val panel = rememberInPage(key) {
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
    } else if (key == Constants.defaultLocal) {
        router.hideWithLocal()
    }
}


/**
 * 自适应面板
 * @param windowWidthSize 当界面宽度大于这个尺寸级别时才显示，如果为空，则一直显示
 * @param windowHeightSize 当界面高度大于这个尺寸级别时才显示，如果为空，则一直显示
 */
@Composable
fun AutoPanel(
    windowWidthSize: WindowWidthSize? = null,
    windowHeightSize: WindowHeightSize? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val windowScope = LocalWindowScope.current
    val windowSize by remember { windowScope.windowSize }
    if ((windowWidthSize == null && windowHeightSize == null) ||
        (windowWidthSize != null && windowSize.width.value > windowWidthSize.value) ||
        (windowHeightSize != null && windowSize.height.value > windowHeightSize.value)
    )
        Box(modifier) {
            content()
        }
}

