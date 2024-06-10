package com.ralf.fragmentbridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ralf.bridge.DefaultCallBackManager
import com.ralf.bridge.ICallbackManager
import com.ralf.bridge.Peer
import com.ralf.fragmentbridge.peer.SecondFirstCallback
import com.ralf.fragmentbridge.peer.SecondPeer
import kotlinx.android.synthetic.main.fragment_second.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SecondFragment : Fragment(), SecondPeer {
    private var param1: String? = null
    private var param2: String? = null
    private val mCallBackManager = DefaultCallBackManager<SecondFirstCallback>()
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
        return inflater.inflate(R.layout.fragment_second, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refresh_1?.setOnClickListener {
            mCallBackManager.allCallBacks {
                it.refreshFirstPage()
            }
        }
    }

    override fun getCallBackManager(clazz: Class<out Peer>): ICallbackManager<*> {
        return mCallBackManager
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String? = null, param2: String? = null) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun refreshPage() {
        i++
        ("已经被fragment 1 通知刷新 i =$i").also {
            second_text?.text = it
        }
    }
}