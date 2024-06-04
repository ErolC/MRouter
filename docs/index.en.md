# MRouter

This is a routing library suitable for `compose-multiplatform`, which implements a series of basic functions such as routing, parameter transfer, animation, gestures, lifecycle, shared elements, and local routing<br>
At present, the library supports `Android`, `iOS`, `JVM`, and `web`

## usage
We first need to create the root of the `Compose` page in `common`, and then implement the entry points for each platform. 

## Prepare two pages
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

### common
Create `RouteHost` in `common`

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

`RouteHost` is the starting point of routing, and `compose` is registered as a page using the `page` method. The above example will first display the `home()` page when opening the app.

## Implement each platform entry

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

=== "desktop"

```kotlin
fun main() = mRouterApplication {
    App()
}
```
=== "ios"

```kotlin
fun MainViewController() = mRouterUIViewController {
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