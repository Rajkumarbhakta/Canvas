import androidx.compose.ui.window.ComposeUIViewController
import com.rkbapps.canvas.App
import com.rkbapps.canvas.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }
