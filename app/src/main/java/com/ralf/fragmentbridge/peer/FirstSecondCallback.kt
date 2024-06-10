package com.ralf.fragmentbridge.peer

import com.ralf.bridge.Peer

/**
 * 第一个页面触发，通知第二个页面
 */
interface FirstSecondCallback : Peer.Callback {

    fun refreshSecondPage()
}