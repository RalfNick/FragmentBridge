package com.ralf.fragmentbridge

import com.ralf.fragmentbridge.peer.FirstSecondBridge
import com.ralf.fragmentbridge.peer.HomeFirstBridge

object BridgeRegister {

    fun registerBridges() {
        HomeFirstBridge.Companion
        FirstSecondBridge.Companion
    }
}