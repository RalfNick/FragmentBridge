package com.ralf.bridge

/** callback管理器的接口 */
interface ICallbackManager<C : Peer.Callback> {

    fun registerCallBack(callback: C)

    fun unRegisterCallBack(callback: C)

    fun getAllCallBack(): Collection<C>

    fun allCallBacks(callback: (C) -> Unit)
}