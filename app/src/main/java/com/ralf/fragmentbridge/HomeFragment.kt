package com.ralf.fragmentbridge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.ralf.fragmentbridge.peer.HomePeer
import kotlinx.android.synthetic.main.fragment_home.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(), HomePeer {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mAdapter: HomeFragmentPagerAdapter

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
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        home_viewpager.offscreenPageLimit = 3
        mAdapter = HomeFragmentPagerAdapter(childFragmentManager)
        home_viewpager.adapter = mAdapter
        tab_layout.addTab(tab_layout.newTab().setText("首页"))
        tab_layout.addTab(tab_layout.newTab().setText("新闻"))
        tab_layout.addTab(tab_layout.newTab().setText("社区"))
        tab_layout.addTab(tab_layout.newTab().setText("动态"))
        tab_layout.addOnTabSelectedListener(object :
            TabLayout.ViewPagerOnTabSelectedListener(home_viewpager) {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                home_viewpager.currentItem = tab?.position ?: 0
            }
        })
        home_viewpager.addOnPageChangeListener(object :
            TabLayout.TabLayoutOnPageChangeListener(tab_layout) {})
        mAdapter.setList(
            listOfNotNull(
                FirstFragment.newInstance(),
                SecondFragment.newInstance(),
                ThirdFragment.newInstance(),
                FourthFragment.newInstance()
            )
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private class HomeFragmentPagerAdapter(manager: FragmentManager) :
        FragmentPagerAdapter(manager) {

        private var list: List<Fragment> = emptyList()

        override fun getCount() = list.size

        override fun getItem(position: Int): Fragment {
            return list[position]
        }

        fun setList(fragments: List<Fragment>) {
            list = fragments
            notifyDataSetChanged()
        }
    }

    override fun navigateToPage(index: Int) {
        home_viewpager?.currentItem = index
    }

    override fun getPageCount(): Int = mAdapter.count

}