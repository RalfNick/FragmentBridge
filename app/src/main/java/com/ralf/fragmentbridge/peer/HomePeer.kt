package com.ralf.fragmentbridge.peer

import com.ralf.bridge.Peer

interface HomePeer : Peer {

    fun navigateToPage(index: Int)

    fun getPageCount(): Int
}