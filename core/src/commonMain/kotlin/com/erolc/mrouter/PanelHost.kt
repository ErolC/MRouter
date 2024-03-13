package com.erolc.mrouter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.erolc.mrouter.backstack.entry.LocalWindowScope
import com.erolc.mrouter.backstack.entry.PageEntry
import com.erolc.mrouter.route.transform.Resume
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
 * @param windowWidthSize 当界面宽度大于这个尺寸级别时才显示，如果为空，则一直显示
 * @param windowHeightSize 当界面高度大于这个尺寸级别时才显示，如果为空，则一直显示
 * @param reserveStartAddress 是否保留初始地址；初始地址默认是空地址，可以选择在跳转到真正的页面时选择是否保留初始地址
 */
@Composable
fun PanelHost(
    key: String = Constants.defaultLocal,
    startRoute: String = Constants.defaultPage,
    windowWidthSize: WindowWidthSize? = null,
    windowHeightSize: WindowHeightSize? = null,
    reserveStartAddress: Boolean = true,
    modifier: Modifier = Modifier
) {
    val scope = LocalPageScope.current
    val panel = rememberInPage(key) {
        scope.createPanel(key, startRoute) ?: throw RuntimeException("弹框内部页面不可使用面板（局部）路由")
    }
    AutoPanel(windowWidthSize, windowHeightSize, modifier) {
        panel.Content(Modifier)
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

