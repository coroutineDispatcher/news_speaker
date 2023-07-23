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
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import com.coroutinedispatcher.newsspeaker.R
import com.coroutinedispatcher.newsspeaker.databinding.FragmentMainBinding
import com.coroutinedispatcher.newsspeaker.ui.textinput.TextInputFragment
import com.coroutinedispatcher.newsspeaker.ui.theme.NewsSpeakerTheme

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private var mainFragmentBinding: FragmentMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainFragmentBinding = FragmentMainBinding.inflate(inflater, container, false)

        requireNotNull(mainFragmentBinding).composeView.setContent {
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
                                requireActivity().supportFragmentManager.commit {
                                    setCustomAnimations(
                                        R.anim.slide_in,
                                        R.anim.fade_out,
                                        R.anim.fade_in,
                                        R.anim.slide_out
                                    )
                                    addToBackStack(TextInputFragment.TAG)
                                    replace(R.id.container, TextInputFragment.newInstance())
                                }
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

        return requireNotNull(mainFragmentBinding).root
    }

    override fun onDestroy() {
        super.onDestroy()
        mainFragmentBinding = null
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
