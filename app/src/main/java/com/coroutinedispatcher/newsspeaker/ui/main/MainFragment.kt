package com.coroutinedispatcher.newsspeaker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.coroutinedispatcher.newsspeaker.R
import com.coroutinedispatcher.newsspeaker.databinding.FragmentMainBinding
import com.coroutinedispatcher.newsspeaker.ui.textinput.TextInputFragment
import com.coroutinedispatcher.newsspeaker.ui.theme.NewsSpeakerTheme

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private val binding: FragmentMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = FragmentMainBinding.inflate(inflater, container, false)

        view.composeView.setContent {
            NewsSpeakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        FloatingActionButton(
                            modifier = Modifier.padding(16.dp),
                            onClick = {
                                requireActivity().supportFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.container, TextInputFragment.newInstance())
                                    .commit()
                            },
                            content = {
                                Icon(Icons.Filled.Add, contentDescription = "Add")
                            },
                            shape = FloatingActionButtonDefaults.extendedFabShape
                        )
                    }
                }
            }
        }

        return view.root
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}