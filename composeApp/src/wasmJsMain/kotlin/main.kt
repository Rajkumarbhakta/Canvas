import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.ui.window.ComposeViewport
import com.rkbapps.canvas.App
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    try {
        val body = document.body ?: return
        ComposeViewport(body) {
            App()
        }
    }catch (e: Exception){
        e.printStackTrace()
        print("ERROR>>>>>>>$e")
    }
}