package com.ralf.fragmentbridge.peer

import com.ralf.bridge.Peer

/**
 * 第二个页面触发，通知第一个页面
 */
interface SecondFirstCallback : Peer.Callback {

    fun refreshFirstPage()
}