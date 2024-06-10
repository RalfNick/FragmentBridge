package com.ralf.fragmentbridge.peer

import androidx.fragment.app.Fragment
import com.ralf.bridge.Bridge
import com.ralf.bridge.BridgeConfig
import com.ralf.bridge.Peer

class FirstSecondBridge : Bridge<FirstSecondPeer, SecondPeer>(), FirstSecondCallback,
    SecondFirstCallback {


    companion object {
        init {
            BridgeConfig.register(FirstSecondPeer::class.java,
                SecondPeer::class.java,
                FirstSecondBridge::class.java)
        }
    }

    override fun isConnectPermitted(first: Peer, second: Peer): Boolean {
        if (first !is Fragment || second !is Fragment) {
            return false
        }
        return when {
            first.parentFragment == second -> {
                false
            }
            first == second.parentFragment -> {
                false
            }
            else -> true
        }
    }

    override fun refreshSecondPage() {
        getSecondPeer()?.refreshPage()
    }

    override fun refreshFirstPage() {
        getFirstPeer()?.refreshPage()
    }
}