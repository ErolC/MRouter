# Lifecycle&ViewModel
Lifecycle是由Android推出的用于描述页面生命周期的库，现已扩充至支持多平台，用户可通过给LifecycleOwner设置Observer以监听页面生命周期变化，具体的可参考官方文章：[Lifecycle](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-lifecycle.html)。

本库提供方便监听生命周期的函数`LifecycleObserver`:
```kotlin
@Composable
fun Page(){
LifecycleObserver { _, event ->
        loge("tag", "$event")
    }
}
```

ViewModel也是由Android推出的，是用于分离不同的代码，让各个类变得更加单一，具体也可参考官方文章：[ViewModel](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-viewmodel.html)

同样的，为了方便使用，也提供了一个方法`viewModel`:
```kotlin
@Composable
fun Page(){
    val viewModel = viewModel(::FirstViewModel)
}
```
该方法只能用于构造无参的ViewModel或有唯一参数`SavedStateHandle`的ViewModel，如果有更加复杂的构造需求，建议使用依赖注入框架，比如：[koin](https://insert-koin.io/)。