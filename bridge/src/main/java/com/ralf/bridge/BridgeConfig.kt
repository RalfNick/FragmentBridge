package com.ralf.bridge

/** Peer 信息的注册类 */
object BridgeConfig {

    private val sConnectedPeers = mutableMapOf<Class<out Peer>, Set<PeerClassInfo<*, *>>>()

    fun register(
        firstPeer: Class<out Peer>,
        secondPeer: Class<out Peer>,
        bridge: Class<out Bridge<*, *>>,
    ) {
        sConnectedPeers[firstPeer].isNullOrEmpty().let {
            if (!it) {
                return
            }
            sConnectedPeers[firstPeer] =
                mutableSetOf<PeerClassInfo<*, *>>(PeerClassInfo(secondPeer, bridge))
        }
        sConnectedPeers[secondPeer].isNullOrEmpty().let {
            if (!it) {
                return
            }
            sConnectedPeers[secondPeer] =
                mutableSetOf<PeerClassInfo<*, *>>(PeerClassInfo(firstPeer, bridge))
        }
    }

    fun getConnectPeers(peerClass: Class<out Peer>): Set<PeerClassInfo<*, *>>? {
        return sConnectedPeers[peerClass]
    }
}

/**
 * 为便于检索bridge信息而存在的model类，equals和hashcode以组合实例mPeerEnvClass为准（hashset中判断依据为mPeerEnvClass）
 */
class PeerClassInfo<P : Peer, B : Bridge<*, *>> constructor(
    val peerInterface: Class<P>,
    val bridgeClass: Class<B>,
) {
    override fun equals(other: Any?) = when (other) {
        is PeerClassInfo<*, *> -> peerInterface == other.peerInterface
        else -> false
    }

    override fun hashCode() = peerInterface.hashCode()
}