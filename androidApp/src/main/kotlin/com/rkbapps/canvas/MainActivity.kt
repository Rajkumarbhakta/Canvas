package com.rkbapps.canvas

import android.content.ContentResolver
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rkbapps.canvas.util.IncomingFiles
import com.rkbapps.canvas.util.readDrawingFile
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val incomingFiles: IncomingFiles by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Only on a fresh start: after a restore the same intent comes back, already handled.
        if (savedInstanceState == null) openDrawingFile(intent)
        setContent { App() }
    }

    // The activity is singleInstance, so a file opened while the app is running arrives here.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        openDrawingFile(intent)
    }

    /** A Canvas file opened from a file manager, a chat or "Open with": hand it to the home screen to import. */
    private fun openDrawingFile(intent: Intent?) {
        if (intent?.action != Intent.ACTION_VIEW) return
        // Reopening the app from Recents repeats the old intent; the file was imported the first time.
        if (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY != 0) return
        val uri = intent.data?.takeIf { it.scheme == ContentResolver.SCHEME_CONTENT } ?: return
        val context = applicationContext
        incomingFiles.open { context.readDrawingFile(uri) }
    }
}
