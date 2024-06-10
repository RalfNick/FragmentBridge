package com.ralf.bridge

import android.content.Context
import android.util.Log
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import java.lang.ref.WeakReference

class BridgeManager : FragmentManager.FragmentLifecycleCallbacks() {

    companion object {

        @JvmStatic
        private val mManager = mutableMapOf<Int, BridgeManager>()

        @JvmStatic
        @MainThread
        fun install(activity: FragmentActivity) {
            val identity = System.identityHashCode(activity)
            activity.run {
                if (mManager[identity] == null) {
                    val manager = BridgeManager()
                    supportFragmentManager.registerFragmentLifecycleCallbacks(manager, true)
                    mManager[identity] = manager
                }
            }
        }

        @JvmStatic
        @MainThread
        fun unInstall(activity: FragmentActivity) {
            activity.apply {
                val identity = System.identityHashCode(this)
                mManager[identity]?.let {
                    supportFragmentManager.unregisterFragmentLifecycleCallbacks(it)
                    mManager.remove(identity)
                }
            }
        }
    }

    private val mBridgerSet = mutableMapOf<String, MutableSet<Bridge<*, *>>>()
    private val mPeerInterfaceMap =
        mutableMapOf<Class<out Peer>, MutableSet<ComparableWeakReference<Peer>>>()

    override fun onFragmentAttached(fm: FragmentManager, f: Fragment, context: Context) {
        super.onFragmentAttached(fm, f, context)
        if (f !is Peer) return
        (f as Peer).apply {
            getPeerInterfaces(this.javaClass).forEachIndexed { _, clazz ->
                if (mPeerInterfaceMap[clazz] == null) {
                    mPeerInterfaceMap[clazz] = mutableSetOf()
                }
                mPeerInterfaceMap[clazz]?.add(ComparableWeakReference(this))
                BridgeConfig.getConnectPeers(clazz)?.forEachIndexed { _, peerClassInfo ->
                    mPeerInterfaceMap[peerClassInfo.peerInterface]?.forEachIndexed { _, anotherPeer ->
                        connectBridge(this, anotherPeer.get(), peerClassInfo.bridgeClass)
                    }
                }
            }
        }
    }

    override fun onFragmentDetached(fm: FragmentManager, f: Fragment) {
        super.onFragmentDetached(fm, f)
        if (f !is Peer) {
            return
        }
        (f as Peer).apply {
            getPeerInterfaces(this.javaClass).forEachIndexed { _, clazz ->
                mPeerInterfaceMap[clazz]?.remove(ComparableWeakReference(this))
                BridgeConfig.getConnectPeers(clazz)?.forEachIndexed { _, peerClassInfo ->
                    mPeerInterfaceMap[peerClassInfo.peerInterface]?.forEachIndexed { _, anotherPeer ->
                        disConnectBridge(this, anotherPeer.get())
                    }
                }
            }
        }
    }

    private fun connectBridge(first: Peer?, second: Peer?, bridgeClass: Class<out Bridge<*, *>>) {
        if (first == null || second == null) {
            return
        }
        val bridgeKey = getBridgeKey(first, second)
        val bridgeSet = mBridgerSet[bridgeKey]
        if (bridgeSet == null) {
            mBridgerSet[bridgeKey] = mutableSetOf()
        }
        mBridgerSet[bridgeKey]?.forEachIndexed { _, connectedBridge ->
            if (connectedBridge.javaClass == bridgeClass) {
                return
            }
        }
        try {
            val bridge = bridgeClass.newInstance()
            if (bridge.connect(first, second)) {
                bridgeSet?.add(bridge)
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        }
    }

    private fun disConnectBridge(first: Peer?, second: Peer?) {
        if (first == null || second == null) {
            return
        }
        val bridgeKey = getBridgeKey(first, second)
        mBridgerSet[bridgeKey]?.run {
            this.forEachIndexed { _, bridge ->
                bridge.disConnect(first, second)
            }
            mBridgerSet.remove(bridgeKey)
        }
    }

    private fun getPeerInterfaces(peerClass: Class<*>?): Set<Class<out Peer>> {
        val peerSet = mutableSetOf<Class<out Peer>>()
        var peerClazz = peerClass

        while (peerClazz != null) {
            Log.d("BridgeManager", "getPeerInterfaces")
            peerClazz.interfaces.forEachIndexed { _, clazz ->
                if (Peer::class.java.isAssignableFrom(clazz)) {
                    peerSet.add(clazz as Class<out Peer>)
                }
            }
            peerClazz = peerClazz.superclass ?: null
        }
        return peerSet
    }

    /**
     * 根据两个peer实例生成一个id（为避免同样两个实例传入次序不同造成id不同，id生成过程中指定了次序）
     */
    private fun getBridgeKey(first: Peer, second: Peer): String {
        val firstKey = System.identityHashCode(first)
        val secondKey = System.identityHashCode(second)
        return when {
            firstKey < secondKey -> "${firstKey}_$secondKey"
            else -> "${secondKey}_$firstKey"
        }
    }
}

/**
 * wrapper类，equals和hashcode使用wrap的真实引用对象的，这样可以同时使用弱引用的特性，又可以使用hashset
 */
class ComparableWeakReference<T>(private val referent: T) : WeakReference<T>(referent) {

    override fun equals(other: Any?): Boolean = when (other) {
        is ComparableWeakReference<*> -> other.get() == referent
        else -> false
    }

    override fun hashCode(): Int = referent?.hashCode() ?: 0
}