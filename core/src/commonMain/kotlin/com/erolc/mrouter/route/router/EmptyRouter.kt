package com.erolc.mrouter.route.router

import com.erolc.mrouter.model.Route

/**
 * 空导航，对于末端的页面来说，本身是不存在导航功能的，所以他的导航事件则直接交给父类处理即可
 */
@Deprecated("废弃")
class EmptyRouter(override val parentRouter: Router) : Router {
    override fun router(route: Route) {
        dispatchRoute(route)
    }

    override fun dispatchRoute(route: Route) {
        parentRouter.dispatchRoute(route)
    }

    override fun backPressed(notInterceptor: () -> Boolean) {
        parentRouter.backPressed(notInterceptor)
    }
}