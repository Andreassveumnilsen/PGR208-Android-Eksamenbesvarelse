package no.kristiania.pgr208finalexam.service

import android.os.Handler
import android.os.Looper

class QueueingService {

    val mainHandler: Handler = Handler(Looper.getMainLooper())

    fun startQueue(functionToRun: () -> Unit) {
        mainHandler.post(object : Runnable {
            override fun run() {
                functionToRun()
                mainHandler.postDelayed(this, 3000)
            }
        })
    }

    fun stopQueue() {
        if (!mainHandler.looper.queue.isIdle)
            mainHandler.removeCallbacksAndMessages(null)
    }

}