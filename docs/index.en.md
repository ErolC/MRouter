# MRouter

This is a routing library suitable for `compose-multiplatform`, which implements a series of basic functions such as routing, parameter transfer, animation, gestures, lifecycle, shared elements, and local routing<br>
At present, the library supports `Android`, `iOS`, `JVM`, and `web`

## usage
We first need to create the root of the `Compose` page in `common`, and then implement the entry points for each platform. 

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
### android

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App() // Using the common App()
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

Due to the need to manage `window`, define from `application` onwards. That's right, `desktop` can run in multiple windows when using this library.

### ios

```kotlin
fun MainViewController() = LifecycleUIViewController {
    App()
}
```

Due to the need to manage the lifecycle of pages, `iOS` also needs to do the same.

### web

```kotlin
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        App()
    }
}
```

Please note that `compose-wasm` is currently experimental and may have more bugs than other platforms. Please use it with caution.