package com.ralf.fragmentbridge.peer

import com.ralf.bridge.Bridge
import com.ralf.bridge.BridgeConfig

class HomeFirstBridge : Bridge<HomePeer, FirstHomePeer>(), FirstHomeCallback {

    override fun navigateToPage(index: Int) {
        if (index < 0) {
            return
        }
        getFirstPeer()?.run {
            if (index < getPageCount()) {
                navigateToPage(index)
            }
        }
    }

    companion object {
        init {
            BridgeConfig.register(HomePeer::class.java,
                FirstHomePeer::class.java,
                HomeFirstBridge::class.java)
        }
    }
}
