package com.ralf.bridge

import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy

/**
 * 定义为连接两个peer对象，以便使两个peer对象可以通过各自回调进行通信的一个机制。
 * 抽象类，需要
 * 1. 看到双方peer的Env接口
 * 2. 实现双方peer的Callback接口，
 * 参与通信的两个peer在通信中仅仅关注自身状态变化，将变化信息通过callback的形式发出去，
 * bridge接收A的callback调用并决定要调用B的Env暴露出来的某个方法
 */
open class Bridge<FP : Peer, SP : Peer> : Peer.Callback {

    private var mFirstPeer: WeakReference<FP>? = null
    private var mSecondPeer: WeakReference<SP>? = null
    private var mFirstCallbackManager: ICallbackManager<Peer.Callback>? = null
    private var mSecondCallbackManager: ICallbackManager<Peer.Callback>? = null
    private var mFirstPeerClass: Class<*>? = null
    private var mSecondPeerClass: Class<*>? = null
    private var mFirstPeerNop: FP? = null
    private var mSecondPeerNop: SP? = null

    /**
     * 通信双方凑齐时bridge连接会建立，此方法将会调用，子类可按需求决定是否复写监听此时机
     */
    open fun onConnected() {}

    /**
     * 通信双方有一方退出时bridge连接断开，此方法会调用，子类可按需求决定是否复写监听此时机
     */
    open fun onDisConnected() {}

    @Suppress("UNREACHABLE_CODE")
    fun connect(one: Peer?, another: Peer?): Boolean {
        if (one == null || another == null) {
            return false
        }
        var first = one
        var second = another
        ensurePeerInterface()
        if (mFirstPeerClass?.isAssignableFrom(first.javaClass) != true) {
            first = another
            second = one
        }
        if (isConnectPermitted(first, second)) {
            mFirstPeer = WeakReference<FP>(first as FP)
            mSecondPeer = WeakReference<SP>(second as SP)
            mFirstCallbackManager = mFirstPeerClass?.let { first.getCallBackManager(it as Class<out Peer>) } as ICallbackManager<Peer.Callback>?
            mFirstCallbackManager?.apply {
                this.registerCallBack(getFirstCallback())
            }
            mSecondCallbackManager = mSecondPeerClass?.let { second.getCallBackManager(it as Class<out Peer>) } as ICallbackManager<Peer.Callback>?
            mSecondCallbackManager?.apply {
                this.registerCallBack(getSecondCallback())
            }
            onConnected()
            return true
        }

        return false
    }

    open fun isConnectPermitted(first: Peer, second: Peer): Boolean {
        if (first is Fragment && second is Fragment) {
            var parent = second.parentFragment
            while (parent != null) {
                if (first == parent) {
                    return true
                }
                parent = parent.parentFragment
            }
            parent = first.parentFragment
            while (parent != null) {
                if (second == parent) {
                    return true
                }
                parent = parent.parentFragment
            }
        }
        return false
    }

    private fun ensurePeerInterface() {
        if (mFirstPeerClass != null && mSecondPeerClass != null) {
            return
        }
        ((javaClass.genericSuperclass) as ParameterizedType).actualTypeArguments.apply {
            val f = this[0] as Class<*>
            val s = this[1] as Class<*>
            if (f.isInterface && s.isInterface) {
                mFirstPeerClass = this[0] as Class<*>?
                mSecondPeerClass = this[1] as Class<*>?
            }
        }
    }

    fun disConnect(one: Peer?, another: Peer?) {
        if (one == null || another == null) {
            return
        }
        onDisConnected()
        mFirstCallbackManager?.unRegisterCallBack(getFirstCallback())
        mFirstPeer = null
        mSecondCallbackManager?.unRegisterCallBack(getSecondCallback())
        mSecondPeer = null
    }

    /**
     * 返回FirstPeer对应的Callback,默认bridge实现双方的callback因此返回this，如果使用方想对默认逻辑进行定制可以复写此方法
     *
     *
     * 定制情形举例：
     * 1. 双方callback接口有签名冲突，bridge不可同时实现双方，需要用组合的方式持有双方callback实现
     * 2. bridge希望可以不实现所有双方的callback，需要复写此方法并返回空即可
     *
     * @return
     */
    open fun getFirstCallback(): Peer.Callback {
        return this
    }

    /**
     * 返回SecondPeer对应的Callback,默认bridge实现双方的callback因此返回this，如果使用方想对默认逻辑进行定制可以复写此方法
     *
     *
     * 定制情形举例：
     * * 1. 双方callback接口有签名冲突，bridge不可同时实现双方，需要用组合的方式持有双方callback实现
     * * 2. bridge希望可以不实现所有双方的callback，需要复写此方法并返回空即可
     *
     * @return
     */
    open fun getSecondCallback(): Peer.Callback {
        return this
    }

    fun getFirstPeer(): FP? = mFirstPeer?.get()

    fun getSecondPeer(): SP? = mSecondPeer?.get()

    @Suppress("UNCHECKED_CAST")
    fun getFirstPeerOrNop(): FP {
        return mFirstPeer?.get()
            ?: mFirstPeerNop
            ?: (Proxy.newProxyInstance(javaClass.classLoader, arrayOf(mFirstPeerClass),
                InvocationHandler { _, _, _ ->
                    return@InvocationHandler null
                }) as FP).also { mFirstPeerNop = it }
    }

    @Suppress("UNCHECKED_CAST")
    fun getSecondPeerOrNop(): SP {
        return mSecondPeer?.get()
            ?: mSecondPeerNop
            ?: (Proxy.newProxyInstance(javaClass.classLoader, arrayOf(mSecondPeerClass),
                InvocationHandler { _, _, _ ->
                    return@InvocationHandler null
                }) as SP).also { mSecondPeerNop = it }
    }
}