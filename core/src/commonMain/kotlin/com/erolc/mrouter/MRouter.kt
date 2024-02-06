package com.erolc.mrouter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.erolc.mrouter.backstack.BackStack
import com.erolc.mrouter.backstack.StackEntry
import com.erolc.mrouter.backstack.WindowEntry
import com.erolc.mrouter.model.WindowOptions
import com.erolc.mrouter.register.RegisterBuilder
import com.erolc.mrouter.route.Router


class MRouter private constructor() {

    private lateinit var router: Router

    /**
     * 主后退栈，也是window后退栈，window节点将会保存在这里。
     * 主后退栈一般只有一个值
     */

    companion object {
        internal fun getMRouter(startTarget: String,windowOptions: WindowOptions, builder: RegisterBuilder.() -> Unit): MRouter {
            return MRouter().apply {
                router = RegisterBuilder().apply(builder).builder(startTarget,windowOptions)
            }
        }
    }

    @Composable
    internal fun getRootBlackStack(): State<List<StackEntry>> {
        return router.getBackStack().collectAsState(listOf<WindowEntry>())
    }
}