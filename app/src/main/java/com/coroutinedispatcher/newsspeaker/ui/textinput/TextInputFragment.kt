package com.coroutinedispatcher.newsspeaker.ui.textinput

import android.os.Bundle
import androidx.fragment.app.Fragment

class TextInputFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        error("IllegalStateException")
    }

    companion object {
        fun newInstance() = TextInputFragment()
    }
}