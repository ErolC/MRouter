import com.erolc.mrouter.route.ResourcePool
import kotlin.test.Test

class Test {

    @Test
    fun test(){
        val match = Regex("\\{[^/]+}").find("/test/test1/{test}") != null
        println(match)
    }
}