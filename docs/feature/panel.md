# 局部路由

当页面足够大时，一个窗口应该不止显示一个页面，这时就需要局部路由了，最经典的就是列表+详情页面了。

## PanelHost

在一个页面中使用`PanelHost`便可在这个页面使用局部路由

```kotlin
 Row {
    Column(
        Modifier.background(Color.Blue).fillMaxSize().weight(1f).zIndex(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            route("local:sample/first") {
//                    panel("local", false)
            }
        }) {
            Text("first")
        }
    }
    PanelHost(modifier = Modifier.weight(2f), panelState = rememberPanelState(), onPanelChange = {
        loge("tag", "isAttach:$it")
    })
}
```

通过panelState可以配置当前的panel和页面大小的关系，比如说当页面处于某个尺寸下才显示<br>
通过onPanelChange回调可以接收这个panel是否显示，现在就可以将页面路由到这个panel上了<br>

```kotlin
 Button(onClick = {
    route("local:sample/first") {
//                    panel("local", false)
    }
}) {
    Text("first")
}
```

通过给路径加前缀`local:`或者使用RouteBuild的`panel`
方法都可以在路由时将地址路由到panel中，其中的local就是这个panel的key。<br>
如果当前页面并不存在key为local的panel，那么将和普通的路由别无二致。
## 局部路由到主路由
在局部内使用路由，可以在路径上加`root`前缀，比如：`root:path`，这样path对应的地址将路由到主路由（window下的首层page）<br>
因此局部路由创建是不可使用root作为key。

## HostSize

HostSize指示Host的尺寸规则和WindowSize类似，都分为：`Compact`，`Medium`和`Expanded`。<br>
由于panel是可嵌套的，那么位于panel内的panel就需要以外部的panel的大小作为尺寸指示而非windowSize。<br>
关于WindowSize相关，可以参考[窗口大小类别](https://developer.android.com/develop/ui/compose/layouts/adaptive/window-size-classes?hl=zh-cn)
> 嵌套panel：当将一个有panel的页面路由到当前页面的panel中，那么就会形成嵌套panel。
