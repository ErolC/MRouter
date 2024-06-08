# 共享控件
## 简单共享元素
使用共享元素时，只需在两个页面中使用`Element`函数，两者使用相同的key，这就是共享的元素，然后在`route`时设置`transform=share("key")`即可。
```kotlin
//page1
Button(onClick={
    pageScope.route("page2"){
        transform = share("ele_key")
    }
})

Element("ele_key"){
    //code...
}

//page2
Element("ele_key"){
    //code...
}
```
共享元素是支持多个的，只需要在`share`方法中指明即可，没有指明的Element将不执行共享（和普通的控件没有区别）
## 共享元素内部变换
上述是简单的共享元素使用，当你需要共享元素内部的样式也需要在共享过程中进行变换，比如希望共享元素的背景颜色可以顺滑的过渡，那么需要进阶一下。
```kotlin
//page1
Button(onClick={
    pageScope.route("page2"){
        transform = share("ele_key")
    }
})

Element("ele_key",styles=listOf(Color.Red)){
    val bgColor by getStyle(0, animate = animateColor())
    //code...
}

//page2
Element("ele_key",styles=listOf(Color.Blue)){
    val bgColor by getStyle(0, animate = animateColor())
    //code...
}
```
这样共享元素内部直接使用`bgColor`即可。除了color，还支持Int，Offset等多种值，如果有特殊的值，可使用`animateValue`方法进行自定义。
当需要共享元素内部变换响应手势，只需要给`getStyle`方法设置sharing即可：
```
    val bgColor by getStyle(0,sharing = ::sharing, animate = animateColor())
```
如此，bgColor的值将会随着手势的进度而变化。
## 更新共享元素
当存在多个共享元素时，以A共享元素进入，可以通过更新共享元素使得可以使用B共享元素退出。
```kotlin
//page2
 updateElement("label")

 //page1
 onUpdateElement{
    //code...
 }
```
使用`updateElement`方法即可替换当前使用的共享元素，如果替换的共享元素并未找到，那么将会通过`onUpdateElement`回调方法通知给前一个页面，让其准备好。
## 官方的共享元素。
在我编写MRouter的时候官方是还没有推出共享元素的，但是到现在，官方已经推出了，是两种不同的实现，待官方的稳定之后也会考虑集成进来。[官方的共享元素](https://developer.android.google.cn/develop/ui/compose/animation/shared-elements?hl=zh-cn)。