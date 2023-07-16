package com.coroutinedispatcher.newsspeaker.ui.textinput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.fragment.app.Fragment
import com.coroutinedispatcher.newsspeaker.databinding.FragmentTextInputBinding

class TextInputFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTextInputBinding.inflate(inflater, container, false)

        binding.textInputComposable.setContent {
            Surface() {
                Text(text = "Hello from Text Input Screen")
            }
        }
        
        return binding.root
    }

    companion object {
        fun newInstance() = TextInputFragment()
    }
}