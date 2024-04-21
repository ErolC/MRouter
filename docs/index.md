# MRouter

这是一个适用于`compose-multiplatform`的路由库，其实现了基础的路由，参数传递，动画，手势，生命周期，共享元素以及局部路由等一系列功能。<br>
目前该库支持`android`，`ios`，`JVM`和`web`

## 用法
我们首先需要在`common`中创建`Compose`页面的根部，然后实现各个平台的入口即可。

### common
在`common`中创建`RouteHost`

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

`RouteHost`是路由的起点，通过`page`方法将`composable`注册成页面，以上示例在打开app时将首先展现`home()`页面。
### android

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

### desktop

```kotlin
fun main() = mRouterApplication {
    App()
}
```

由于需要管理`window`，所以从`application`开始定义。没错，`desktop`在使用该库是可以多窗口运行的。
### ios

```kotlin
fun MainViewController() = LifecycleUIViewController {
    App()
}
```

由于需要管理页面的生命周期，ios也需如此。

### web

```kotlin
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        App()
    }
}
```
需要注意`compose-wasm`正处于实验性阶段，因此可能会有比其他平台更多的bug，请谨慎使用。