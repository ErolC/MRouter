# MRouter

这是一个适用于`compose-multiplatform`的路由库，其实现了基础的路由，参数传递，动画，手势，生命周期，共享元素以及局部路由等一系列功能。<br>
目前该库支持`android`，`ios`，`JVM`和`web`


## 使用

我们首先需要在common中创建Compose页面的根部，然后实现各个平台的入口即可。代码如下

## 准备两个页面
```kotlin
@Composable
fun Home(){
//code...
}

@Composable
fun Second(){
//code...
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
## 实现各平台入口



=== "android"

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
=== "ios"

    ```kotlin
    fun MainViewController() = mRouterUIViewController {
        App()
    }
    ```

=== "desktop"

    ```kotlin
    fun main() = mRouterApplication {
        App()
    }
    ```

=== "wasmJs"

    ```kotlin
    @OptIn(ExperimentalComposeUiApi::class)
    fun main() {
        CanvasBasedWindow(canvasElementId = "ComposeTarget") {
            App()
        }
    }
    ```