package com.rkbapps.canvas.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Canvas files handed to the app from outside: tapped in a file manager, picked with
 * "Open with", or double-clicked on desktop.
 *
 * The platform entry points (`MainActivity` on Android, `main` on desktop) put files
 * in; the home screen takes them out and imports them. Files wait here until the home
 * screen is ready, so one that launched the app is not lost.
 */
class IncomingFiles {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val pending = Channel<ByteArray?>(Channel.UNLIMITED)

    /** One item per opened file: its content, or `null` when it could not be read. */
    val files: Flow<ByteArray?> = pending.receiveAsFlow()

    /** [read] runs off the main thread; returning `null` or throwing means "could not be read". */
    fun open(read: () -> ByteArray?) {
        scope.launch {
            pending.send(runCatching(read).getOrNull())
        }
    }
}
