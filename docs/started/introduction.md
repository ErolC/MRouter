# 初次使用
本章节将介绍如何简单使用MRouter。


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
    fun MainViewController() = ComposeUIViewController {
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
## 路由传递参数
```kotlin
    //pageA
    pageScope.route("second?query=1"){
        argBuild{
            putString("key","value")
        }
    }

    //pageB
    val args = rememberArgs()
    args.getString("key") // value
    args.getInt("query") //1
```
## 路由过渡动画
```kotlin
    pageScope.route("second?query=1"){
        transform{
            enter = slideInHorizontally { it } // enter from right to left and exit from left to right
        }
    }
```