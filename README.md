
# MRouter

[MRouter](https://erolc.github.io/MRouter)是一个适用于[compose-multiplatform](https://github.com/JetBrains/compose-multiplatform)的路由库，其实现了基础的路由，参数传递，动画，手势，生命周期，共享元素以及局部路由等一系列功能。


## 安装
在项目的`build.gradle.kts`中添加以下依赖，同步后即可开始使用
```kotlin
    commonMain.dependencies {
            implementation("cn.erolc.mrouter:core:1.0.0-beta")
    }
```

## 使用

我们首先需要在common中创建Compose页面的根部，然后实现各个平台的入口即可。代码如下

## 准备两个页面
```kotlin
@Composable
fun Home(){
    val pageScope = LocalPageScope.current
    Button(onClick={
        pageScope.route("second") // route to second page
    }){
        //code...
    }
}

@Composable
fun Second(){
    val pageScope = LocalPageScope.current
    Button(onClick={
        pageScope.backPressed() // back to home page
    }){
        //code...
    }
}
```

## common
在`common`中使用`RouteHost`

```kotlin
@Composable
fun App() {
    MaterialTheme {
        RouteHost("home") {
            page("home") {
                Home()
            }
            page("second") {
                Second()
            }
        }
    }
}
```

`RouteHost`是路由的起点，通过`page`方法将`composable`注册成页面，以上示例在打开app时将首先展现`Home()`页面。有关注册的更多操作，可前往[注册](https://erolc.github.io/MRouter/route/register.html)部分。


## android

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App() // 使用common的App()
        }
    }
}
```

## desktop

```kotlin
fun main() = mRouterApplication {
    App()
}
```
## ios

```kotlin
fun MainViewController() = mRouterUIViewController {
    App()
}
```
## wasmJs

```kotlin
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        App()
    }
}
```
