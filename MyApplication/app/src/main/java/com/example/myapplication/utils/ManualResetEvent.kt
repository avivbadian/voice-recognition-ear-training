package com.example.myapplication.utils

class ManualResetEvent {

    private val monitor = Object()

    /**
     * Volatile = work against main memory and not CPU cache
     * guarantees visibility of changes to variables across threads
      */
    @Volatile
    private var open : Boolean = false

    constructor(open: Boolean) {
        this.open = open
    }

    @Throws(InterruptedException::class)
    fun waitOne()  {
        synchronized(monitor) {
            while (!open) {
                monitor.wait()
            }
        }
    }

    @Throws(InterruptedException::class)
    fun waitOne(milliseconds: Long) : Boolean {
        synchronized(monitor) {
            if (open)
                return true
            monitor.wait(milliseconds)
            return open
        }
    }

    fun set() {
        synchronized(monitor) {
            open = true
            monitor.notifyAll()
        }
    }

    fun reset(){
        open = false
    }
}