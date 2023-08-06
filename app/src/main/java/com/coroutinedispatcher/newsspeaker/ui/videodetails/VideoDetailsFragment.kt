package com.coroutinedispatcher.newsspeaker.ui.videodetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coroutinedispatcher.newsspeaker.databinding.FragmentVideoDetailsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoDetailsFragment : Fragment() {
    private val viewModel by viewModels<VideoDetailsViewModel>()
    private var videoDetailsFragmentBinding: FragmentVideoDetailsBinding? = null
    private val binding: FragmentVideoDetailsBinding
        get() = checkNotNull(videoDetailsFragmentBinding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        videoDetailsFragmentBinding =
            FragmentVideoDetailsBinding.inflate(inflater, container, false)

        binding.cvVideoDetails.setContent {
            val state = viewModel.state.collectAsStateWithLifecycle()

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (state.value) {
                    VideoDetailsViewModel.State.Loading -> CircularProgressIndicator()
                    is VideoDetailsViewModel.State.Success -> TODO()
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        videoDetailsFragmentBinding = null
    }
}