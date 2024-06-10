package com.ralf.fragmentbridge.peer

import com.ralf.bridge.Peer

interface FirstHomeCallback : Peer.Callback {

    fun navigateToPage(index: Int)
}