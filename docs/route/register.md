# 注册
通过注册，可以将`composable`注册成独立的页面，可以将一些平台的资源加入到库中，以供在其他地方使用，比如在desktop平台，可以通过注册的方式给窗口添加菜单。
## 使用
你可以在路由库的根部进行注册
```kotlin
RouteHost("firstPage"){
    //在这里进行注册
}
```
也可以在在路由库的根部之前使用MRouter的`register`方法进行注册，需要注意的是，该方法需要在`RouteHost`之前调用。
```kotlin
MRouter.register{
    //在这里进行注册
}
```
该方法主要是用于注册平台特定的页面以及资源。除此之外，你可以在任意地方使用`Register`进行扩展，然后在上述两个地方进行使用也可以完成注册。
## 注册页面
`Register`主要有两个方法可以使用，分别是`page`和`module`。
```kotlin
RouteHost(""){
    page("path?key=value"){
        Page1() // 路径为：path
    }
    module("module1"){
        page("content/{id}"){
            Page1() // 路径为：module1/content/{id}
        }
    }
}
```
`page`方法还可接受一个`PageConfig`参数，用于设定一些页面配置，而页面配置目前只有`launchMode`。<br>
`module`方法可以对一系列拥有相同前缀的路径归组，方便管理。<br>
### 动态路径
库支持动态路径，如上述的：`module1/content/{id}`，其中的`{id}`就是占位符，需要注意的是，如果存在歧义的路由，将以精准的为主，比如：<br>
```kotlin
RouteHost(""){
    page("path/content"){
        Page1() 
    }
     page("path/{name}"){
        Page2() 
    }
}
```
当路由是`path/content`时，将会路由到`Page1`。

## 注册平台资源
除了可以注册`composable`页面之外，还可以注册平台的一些资源，需要使用`RegisterBuilder.registerPlatformResource`方法。对于一些特殊需求，库提供了其他方法，但也是基于该方法实现。
### 注册平台界面
本库除了可以在composable之间路由之外，还可以路由到平台的界面上，如ios的UIViewController，android的Activity以及wasmjs的web页面。这些平台的界面也属于平台资源的一种。

=== "android"

    ```kotlin
    MRouter.register{
        //实现1
        platformRoute("path",ActivityResultContracts.StartActivityForResult()) {
                  it.route(TestActivity::class)
        }
        //实现2
        startActivity("path",TestActivity::class)
        //实现3，跳转到系统设置页面
        setting("app_home",Settings.ACTION_APPLICATION_DETAILS_SETTINGS){
                data = Uri.parse("package:com.erolc.example")
        }
    }
    ```

=== "ios"

    ```kotlin
    MRouter.register{
        //会使用默认的路由方式，如果需要自定义，则需使用registerRouteDelegate注册路由方式
        platformRoute("path", TestUIViewController())
    }
    ```

=== "wasmJs"

    ```kotlin
    MRouter.register{
        platformRoute("path","https://www.baidu.com")
    }
    ```

### 注册菜单
desktop的window是不带菜单的，需要通过`windowMenu`方法进行注册：
```kotlin
MRouter.register{
    windowMenu("windowId"){

    }
}
```
`windowId`是窗口的唯一标识。