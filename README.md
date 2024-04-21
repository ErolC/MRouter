# MRouter

这是一个可用于compose-multiplatform的路由库，其实现了基础的路由，参数传递，动画，手势，生命周期，共享元素以及局部路由等一系列功能。

## 使用

我们首先需要在common中创建Compose页面的根部，然后实现各个平台的入口即可。代码如下

### common

在common中创建RouteHost

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

`RouteHost`是路由的起点，通过`page`方法将`composable`注册成页面，以上示例在打开app时将展现`home()`页面。

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

由于需要管理window，所以从application开始定义。没错，desktop在使用该库是可以多窗口运行的。

### ios

```kotlin
fun MainViewController() = LifecycleUIViewController {
    App()
}
```

由于需要管理页面的生命周期，于是ios需要如上述处理。

### web

```kotlin
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        App()
    }
}
```

需要注意由于compose-wasm正处于实验性。