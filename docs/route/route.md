# 路由
在路由之前，需要先[注册](https://erolc.github.io/MRouter/route/register.html)页面
## 使用
在页面内可通过`LocalPageScope`获取PageScope，然后使用其中的`route`方法进行路由跳转。
```kotlin
val pageScope = LocalPageScope.current
Button(onClick={
    pageScope.route("path"){
        argBuild{
            //edit args
        }
        onResult{
            //返回的值
        }
        window("windowId") // 以窗口的形式打开，目前只有desktop平台有用
        panel("panelId") //在当前界面的对应panel中打开
        transform = normal() // 以normal变换实现页面切换效果
        transform{
            //编辑变换效果。
        }
        flag = NormalFlag //给打开页面设置flag
    }
}){
    //...
}
```
### 参数传递
MRouter提供了灵活的参数传递设置方式，可以使用上述的`argBuild`方法进行设置，也可以将参数放在路径上：`path?key=value`或`path/1`,后者是动态路径，需要在注册的时候使用动态路径的注册方式。

在路径上的参数会尝试变换成正确的数据结构进行传递，比如1会被转换成int类型，如果转换失败，将以字符串的类型进行传递，而为了区分int和long，可在数字后加`L`,显式的转为long类型。

在后退时如果有携带数据，那么将可以在`onResult`回调中获得。

### 多窗口
在desktop平台，可通过`window`方法传入不同的`windowId`打开不同的窗口，窗口的`menu`将通过该id在注册表中寻找。

### 局部路由
在路由时可通过`panel`方法指定该页面路由到当前页面的哪个panel上，除了使用`panel`方法之外，还可以直接在路径上表示`panelId:path`,如果当前页面不存在该panel，将以一个普通页面进行打开，结果和`path`无异。可查看[局部路由](../feature/panel.md)页面

### 变换
在路由时可通过`transform`设置页面变换效果，在路由时可直接使用内置的变换组合，比如<br>
`normal`是从右到左进入，从左到右退出，在界面左侧存在手势区域，可向右滑动退出界面<br>
`modal`是从下到上进入，从上到下退出，效果是ios的`presentViewController`实现的模态效果。且可在界面任意部分下滑退出。<br>
`none`则是无手势版的`normal`。<br>
除此之外，也可以使用第二种方式组合出你想要的变换效果。更详细的使用方式，请参考[变换](../feature/transform.md)
### flag
目前只支持一种`ClearTaskFlag`，即在打开该页面时，清空当前回退栈。

## 回退和拦截
通过`pageScope`的`backPressed`方法即可从当前页面回退到上一个页面，可以使用`setResult`方法设置回退时返回给上一个页面的值；使用`BackHandler`函数可拦截该回退。
```kotlin
@Composable
fun Page(){
    val pageScope = LocalPageScope.current
    BackHandler{
        //intercept the backspace
    }
    Button(onClick={
        pageScope.setResult{
            putInt("key",1)
        }
        pageScape.backPressed()
    }){
        //...
    }
}
```
### 路由到平台界面
MRouter支持路由到各个平台的界面，只需要在各个平台中使用`platformRoute`方法注册平台界面即可。
### 路由到Ios平台页面的补充
> 一般情况下是不需要实现的，库有默认实现提供。

需要路由到ios的UIViewController时，除了需要注册平台界面之外，还需要另外注册两个资源：
1,实现用于跳转的RootViewController。
```kotlin
    MRouter.setRootViewController(rootViewController: UIViewController)
```
2，实现跳转的具体逻辑
```kotlin
MRouter.registerRouteDelegate(delegate: RouteUIViewControllerDelegate)
```
实现RouteUIViewControllerDelegate即可更精细的控制跳转，包括以什么方式跳转，以及传递参数等。

