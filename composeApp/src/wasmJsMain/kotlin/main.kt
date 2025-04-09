import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.rkbapps.canvas.App
import kotlinx.browser.document

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