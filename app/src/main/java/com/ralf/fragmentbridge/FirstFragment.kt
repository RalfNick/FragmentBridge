package com.ralf.fragmentbridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ralf.bridge.DefaultCallBackManager
import com.ralf.bridge.ICallbackManager
import com.ralf.bridge.Peer
import com.ralf.fragmentbridge.peer.FirstHomeCallback
import com.ralf.fragmentbridge.peer.FirstHomePeer
import com.ralf.fragmentbridge.peer.FirstSecondCallback
import com.ralf.fragmentbridge.peer.FirstSecondPeer
import kotlinx.android.synthetic.main.fragment_first.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FirstFragment : Fragment(), FirstHomePeer, FirstSecondPeer {
    private var param1: String? = null
    private var param2: String? = null
    private val mFirstHomeManager = DefaultCallBackManager<FirstHomeCallback>()
    private val mFirstSecondManager = DefaultCallBackManager<FirstSecondCallback>()
    private var i = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jump_social?.setOnClickListener {
            mFirstHomeManager.allCallBacks {
                it.navigateToPage(1)
            }
        }
        refresh_news?.setOnClickListener {
            mFirstSecondManager.allCallBacks {
                it.refreshSecondPage()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
            FirstFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun refreshPage() {
        i++
        ("已经被fragment 2 通知刷新 i =$i").also {
            first_text?.text = it
        }
    }

    override fun getCallBackManager(clazz: Class<out Peer>): ICallbackManager<*>? {
        return when {
            FirstHomePeer::class.java == clazz -> {
                mFirstHomeManager
            }
            FirstSecondPeer::class.java == clazz -> {
                mFirstSecondManager
            }
            else -> null
        }
    }


}