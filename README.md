# MRouter
[![Maven Central](https://img.shields.io/maven-central/v/cn.erolc.mrouter/core?label=MavenCentral&logo=apache-maven)](https://search.maven.org/artifact/cn.erolc.mrouter/core)
[![License](https://img.shields.io/github/license/Kotlin/dokka.svg)](LICENSE.txt)
[![Latest build](https://img.shields.io/github/v/release/ErolC/MRouter?color=orange&include_prereleases&label=latest%20build)](https://github.com/ErolC/MRouter/releases)

[MRouter](https://erolc.github.io/MRouter)是一个适用于[compose-multiplatform](https://github.com/JetBrains/compose-multiplatform)的路由库

其实现了基础的路由，参数传递，动画，手势，生命周期，共享元素以及局部路由等一系列功能。


## 安装
在项目的`build.gradle.kts`中添加以下依赖，同步后即可开始使用
```kotlin
    commonMain.dependencies {
            implementation("cn.erolc.mrouter:core:<version>")
    }
```

## 简单使用

我们首先需要在common中创建Compose页面的根部，然后实现各个平台的入口即可。代码如下

## 准备两个页面
```kotlin
class HomeViewModel : ViewModel() {
    val result = mutableStateOf("")
}

@Composable
fun Home(){
    val pageScope = LocalPageScope.current
    
    val viewModel = viewModel(::HomeViewModel) //use viewModel
    
    var result by remember { viewModel.result }

    Button(onClick={
        pageScope.route("second") { // route to second page
            argBuild{ // build the args
                putString("key","value")
            }
            onResult{
                result = it.getString("result","") //get return data 
            }
        }
    }){
        Text(result) //data
    }
    
}

@Composable
fun Second(){
    val pageScope = LocalPageScope.current
    val args = rememberArgs()
    Button(onClick={
        pageScope.setResult(bundleOf("result" to "success")) // set return data
        pageScope.backPressed() // back to home page
    }){
        Text(args.getString("key")) //value
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

`RouteHost`是路由的起点，通过`page`方法将`composable`注册成页面，以上示例在打开app时将首先展现`Home()`页面.

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
fun MainViewController() = ComposeUIViewController {
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
