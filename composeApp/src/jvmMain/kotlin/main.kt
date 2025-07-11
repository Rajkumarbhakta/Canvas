import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.icon
import java.awt.Dimension
import com.rkbapps.canvas.App
import com.rkbapps.canvas.di.initKoin
import org.jetbrains.compose.reload.DevelopmentEntryPoint
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.core.component.getScopeId

fun main() {
    initKoin()
    application {
        Window(
            title = "Canvas",
            icon = painterResource(Res.drawable.icon),
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
