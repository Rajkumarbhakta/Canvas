import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import com.rkbapps.canvas.App
import com.rkbapps.canvas.di.initKoin
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    initKoin()
    try {
        val body = document.body ?: return
        ComposeViewport(body) {
            val navController = rememberNavController()
            App(navController)
            LaunchedEffect(Unit) {
                window.bindToNavigation(navController)
            }
        }
    }catch (e: Exception){
        e.printStackTrace()
    }
}