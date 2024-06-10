package com.ralf.fragmentbridge

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        BridgeRegister.registerBridges()
    }
}