package com.ralf.bridge

/** 定义为需要通信的一个角色，此角色需要对外暴露供外部调用的方法定义在Peer接口中.
 * 为完成双向通行，还要求此角色提供Callback,自身状态发生改变时通过回调通知外部观察者
 */
interface Peer {

    /**
     * 当前peer的callback，当peer自身状态变化时通过callback对外通知观察者
     */
    interface Callback

    fun getCallBackManager(clazz: Class<out Peer>): ICallbackManager<*>? = null
}