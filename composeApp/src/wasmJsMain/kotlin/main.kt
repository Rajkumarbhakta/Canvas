import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rkbapps.canvas.App
import com.rkbapps.canvas.di.initKoin
import com.rkbapps.canvas.model.SavedDesign
import com.rkbapps.canvas.navigation.Draw
import com.rkbapps.canvas.navigation.Home
import com.rkbapps.canvas.util.json
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    initKoin()
    disableBrowserKeyDefaults()
    try {
        val body = document.body ?: return
        ComposeViewport(body) {
            val navController = rememberNavController()
            App(navController)
            LaunchedEffect(Unit) {
                window.bindToNavigation(navController)
//                { entry ->
//                    val route = entry.destination.route.orEmpty()
//                    when{
//                        route.startsWith(Home.serializer().descriptor.serialName)->{
//                            "#home"
//                        }
//                        route.startsWith(Draw.serializer().descriptor.serialName)->{
//                            val args = entry.toRoute<Draw>()
//                            val data:SavedDesign? = if(args.design!=null) json.decodeFromString(SavedDesign.serializer(),args.design) else null
////                            "#draw/id=${data?.id}"
//                            "#draw"
//                        }
//                        else->{
//                            "#"
//                        }
//                    }
//                }
            }
        }
    }catch (e: Exception){
        e.printStackTrace()
    }
}

@OptIn(ExperimentalJsExport::class)
fun disableBrowserKeyDefaults() {
    js("""
        window.addEventListener('keydown', function(e) {
            const blockedKeys = ["ArrowUp", "ArrowDown", "ArrowLeft", "ArrowRight", "Space", "PageUp", "PageDown", "Home", "End", "F5"];
            
            // Prevent default for blocked keys
            if (blockedKeys.includes(e.key)) {
                e.preventDefault();
            }

            // Prevent Ctrl+S / Cmd+S
            if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 's') {
                e.preventDefault();
            }
        }, { passive: false });
    """)
}
