# 变换
在路由的过程中，MRouter支持设置页面的变换效果，通过`RouteBuild`里的`transform`进行设置。<br>
`transform`接收一个`Transform`对象，可通过`buildTransform`函数构造获得。`Transform`存在四个参数，分别是：enter，exit，popExit，wrap。

## 过渡动画
`enter`，`exit`，`popExit`都是描述页面过渡动画的。<br>
`enter`是新页面进入的动画效果<br>
`exit`旧页面退出的动画效果<br>
`popExit`则是新页面退出的动画效果。其中`popExit`时可缺省的，缺省的时候将会使用`enter`的反效果<br>
旧页面的进入动画效果是不可设置的。
```kotlin
import com.erolc.mrouter.route.transform.slideInHorizontally
import com.erolc.mrouter.route.transform.slideOutHorizontally

fun normal() = buildTransform {
    enter = slideInHorizontally { it }
    exit = slideOutHorizontally { -it / 7 }
    wrap = NormalTransformWrap()
}
```
可以看出使用的`slideInHorizontally`等函数是库实现的，但效果和系统的没有区别，需要了解这些效果的区别可查看：[EnterTransition 和 ExitTransition 示例](https://developer.android.google.cn/develop/ui/compose/animation/composables-modifiers?hl=zh-cn#enter-exit-transition)
## wrap
`TransformWrap`是变换包裹层，这里有三个主要的方法：

`PageContent`:代表当前页面。<br>
`Wrap`:包裹层实现，主要是结合PageContent设置一些所有页面通用且额外的控件或Modifier，比如手势区域。<br>
`prevPageModifier`:前一个页面的Modifier。可设置一次变换过程中前一个页面参与变化的部分。比如`modal`变换时，前一个页面的页面角需要发生圆角变化。

> 在实现`Wrap`时，必须使用`PageContent`函数
