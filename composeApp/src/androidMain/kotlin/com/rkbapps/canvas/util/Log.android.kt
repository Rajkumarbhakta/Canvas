package com.rkbapps.canvas.util

import com.github.panpf.sketch.core.BuildConfig


actual fun logD(tag: String, message: Any) {
    println("\u001B[33m$tag: $message")
}

actual fun logE(tag: String, message: Any,e:Throwable?) {
    println("\u001B[31m"+"$tag: $message : ${e?:""}")
}

actual fun logI(tag: String, message: Any) {
    println("\u001B[34m$tag: $message")
}