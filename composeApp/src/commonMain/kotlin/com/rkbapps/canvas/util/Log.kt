package com.rkbapps.canvas.util

object Log {
    fun d(tag:String, message: Any){
        logD(tag,message)
    }
    fun e(tag:String, message: Any,e:Throwable?=null){
        logE(tag,message,e)
    }
    fun i(tag:String, message: Any){
        logI(tag,message)
    }
}


expect fun logD(tag:String, message: Any)
expect fun logE(tag:String, message: Any,e:Throwable?=null)
expect fun logI(tag:String, message: Any)

