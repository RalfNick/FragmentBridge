package com.ralf.fragmentbridge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ralf.bridge.BridgeManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BridgeManager.install(this)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, HomeFragment.newInstance("", ""))
            .commitAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        BridgeManager.unInstall(this)
    }
}