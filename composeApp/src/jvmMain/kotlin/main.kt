import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension
import com.rkbapps.canvas.App
import com.rkbapps.canvas.di.initKoin
import org.jetbrains.compose.reload.DevelopmentEntryPoint

fun main() {
    initKoin()
    application {
        Window(
            title = "Canvas",
            state = rememberWindowState(width = 800.dp, height = 600.dp),
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(350, 450)
            DevelopmentEntryPoint {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() { App() }
