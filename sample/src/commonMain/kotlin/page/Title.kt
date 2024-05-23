package page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.erolc.mrouter.route.shareelement.Element
import com.erolc.mrouter.scope.LocalPageScope
import com.erolc.mrouter.scope.PageScope
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

//@OptIn(ExperimentalResourceApi::class)
//@Composable
//fun Page(modifier: Modifier = Modifier.background(Color.Transparent), block: @Composable PageScope.() -> Unit) {
//    Column (modifier) {
//        Element("title",Modifier.fillMaxWidth().height(20.dp)){
//            Row {
//                Box(modifier = Modifier.size(40.dp,20.dp).zIndex(1f)){
//                    Image(painterResource(Res.drawable.ic_back_background), contentDescription = "")
//                    Image(painterResource(Res.drawable.ic_back), contentDescription = "", modifier = Modifier.align(Alignment.CenterEnd))
//                }
//            }
//        }
//        Box(Modifier.background(Color.White).fillMaxSize()){
//            val scope = LocalPageScope.current
//            block(scope)
//        }
//    }
//}

@Composable
fun FirstPage(){

}

@Composable
fun SecondPage(){

}