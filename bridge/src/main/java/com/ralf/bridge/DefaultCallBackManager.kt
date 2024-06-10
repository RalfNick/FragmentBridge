package com.ralf.bridge

/** callback管理器的接口默认实现 */
class DefaultCallBackManager<C : Peer.Callback> : ICallbackManager<C> {

    private val mCallBacks = mutableListOf<C>()

    override fun registerCallBack(callback: C) {
        mCallBacks.add(callback)
    }

    override fun unRegisterCallBack(callback: C) {
        mCallBacks.remove(callback)
    }

    override fun getAllCallBack(): Collection<C> = mCallBacks

    override fun allCallBacks(callback: (C) -> Unit) {
        mCallBacks.forEachIndexed { _, c ->
            callback.invoke(c)
        }
    }

}