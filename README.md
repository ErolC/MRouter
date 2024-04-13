## transform
localRouter 既是一个路由器，也是一个页面。他是可以入backStack的。
在界面充足的时候，localRouter将在pageRouter和DialogRouter之间。
当界面不充足时，localRouter需要入栈。

一个矛盾：
LocalRouter之上会有一个dialogrouter吗？
需要。
那么这个dialogrouter就是属于局部的。
也就是只有在局部中导航才能导航到。
如果在局部内，导航到页面的弹框该如何？
在page的角度。local和dialog应该是同属于同一个范畴。同一个等级，


使用mergeRouter作为上面矛盾的解决方案
但因此无法在局部之外调起局部之内的dialog，局部之内倒是可以调起页面的dialog，因为页面包含局部。局部持有页面的路由，但是页面无法持有局部的路由。

mergeRouter的父路由就是pageRouter。所以如果希望将局部的页面放到主路由上是简单的。


router之间的关系：
application直接接触的是一个WindowRouter，这个router用于管理windowEntry

WindowEntry是一个空壳，代表是一个window，里面具体的页面是由pageEntry展示的。
但是windowEntry本身会携带一个router，那就是PageRouter。
这也将是一个window中的主路由器。
PageEntry是负责页面展示的，所有的界面都是。

一个单纯的pageEntry（指的是主路由直接管理的PageEntry）里也会有一个router。这个router就是MergeRouter。

MergeRouter是一个复合型路由，其中管理这这个页面所有的局部页面的后退栈以及这个页面的弹框。

MergeRouter中存在多个回退栈，一个是dialog的，其他都是局部页面的。

PanelEntry是由MergeRouter管理的，它依旧是一个空壳，代表的是面板（一个界面中的局部），一个界面可以由多个面板组成。
PanelEntry也会携带一个PageRouter，单个面板的展示和单个window的展示是类似的，都只需要交给PageEntry就可以了。
而面板之间并不会有所谓的路由，因为都是同一个界面的，决定其是否可以展示是windowSize决定的。
其在界面中的展示需要通过PanelHost，
MergeRouter处理管理当前界面的dialog之外，还管理着panelEntry。
panelEntry中的PageRouter管理的PageEntry也携带一个DialogRouter，可以让DialogRouter只显示在局部。


dialogEntry是一个空壳，代表是一个dialog，里面的具体页面是有pageEntry展示的。
而这个pageEntry会携带一个router，则是EmptyRouter，这个emptyRouter则是末端的路由器，其本身没有任何作用。只做传递路由事件。
如果这个dialogEntry是在MergeRouter中，那么PageEntry携带的router的parentRouter则是MergeRouter。







局部布局有两种，一种是页面中固定存在的，是当前界面一部分，不会随着界面变小而消失的。
另外一种是动态布局，是会随着界面的大小而变动（转移或消失），这里需要关注的是消失的，当界面消失了一部分，剩下的那部分就需要沾满整个界面。
而界面可能是左右结构，也可能是上下结构。是否需要自定义布局？






共享元素：
由两个界面组成，当前所在页面为起始页面，当前所在页面的元素为起始元素，那么目标页面（后退和前进）的元素就是目标元素。
两个页面有两个进度，只能以一个进度为主。
前进和后退就是两种不同的过渡。
PreEnter -> Resume ==> 前进
0-1
Resume -> Exit ==> 后退
1-0
resume -> TransitionState -> exit


并不需要在切换完成之后里面完成transition的替换，应该是在route触发的那一瞬间开始才对。


从什么页面到什么页面
什么页面有什么共享元素
主要分为共享前，共享中，共享后即可
resume - pause 向前，共享元素主要是以resume页面为主。
resume - exit 向后

