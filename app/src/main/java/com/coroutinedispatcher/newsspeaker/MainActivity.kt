package com.coroutinedispatcher.newsspeaker

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.coroutinedispatcher.newsspeaker.ui.main.MainFragment

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }
}