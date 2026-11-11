import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import canvas.composeapp.generated.resources.Res
import canvas.composeapp.generated.resources.icon
import java.awt.Dimension
import com.rkbapps.canvas.App
import com.rkbapps.canvas.di.initKoin
import com.rkbapps.canvas.model.CANVAS_FILE_EXTENSION
import com.rkbapps.canvas.util.IncomingFiles
import com.rkbapps.canvas.util.readDrawingFile
import org.koin.mp.KoinPlatform
import java.awt.Desktop
import java.io.File
import org.jetbrains.compose.resources.painterResource


fun main(args: Array<String>) {
    initKoin()
    openDrawingFiles(args)
    application {
        Window(
            title = "Canvas",
            icon = painterResource(Res.drawable.icon),
            state = rememberWindowState(width = 800.dp, height = 600.dp),
            onCloseRequest = ::exitApplication,
        ) {
            window.minimumSize = Dimension(350, 450)
            App()
        }
    }
}

/**
 * Canvas files the operating system asked the app to open (double-click, "Open with"):
 * Windows and Linux pass them as arguments, macOS sends an open-file event. They are
 * handed to the home screen to import.
 *
 * The file association itself is declared in `build.gradle.kts` and only exists in
 * packaged builds.
 */
private fun openDrawingFiles(args: Array<String>) {
    val incomingFiles = KoinPlatform.getKoin().get<IncomingFiles>()
    fun open(file: File) {
        if (file.isFile && file.extension.equals(CANVAS_FILE_EXTENSION, ignoreCase = true)) {
            incomingFiles.open { readDrawingFile(file) }
        }
    }

    args.forEach { open(File(it)) }
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.APP_OPEN_FILE)) {
        Desktop.getDesktop().setOpenFileHandler { event -> event.files.forEach(::open) }
    }
}
