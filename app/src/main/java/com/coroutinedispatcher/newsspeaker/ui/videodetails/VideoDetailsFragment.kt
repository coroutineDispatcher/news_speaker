package com.coroutinedispatcher.newsspeaker.ui.videodetails

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.os.bundleOf
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
        val projectId = arguments?.getLong(VIDEO_DETAILS_FRAGMENT_TAG)

        binding.cvVideoDetails.setContent {
            val state = viewModel.state.collectAsStateWithLifecycle()

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (state.value) {
                    VideoDetailsViewModel.State.Loading -> CircularProgressIndicator()
                    // TODO if not necessary the callbacks can be deleted later
                    is VideoDetailsViewModel.State.Success -> VideoPlayerComposable(
                        videoUrl =
                        (state.value as VideoDetailsViewModel.State.Success).project.videoPath,
                        onVideoIdle = {
                            Log.d(VIDEO_DETAILS_FRAGMENT_TAG, "Video Started")
                        },
                        onVideoEnded = {
                            Log.d(VIDEO_DETAILS_FRAGMENT_TAG, "Video Ended")
                        },
                        onVideoLoading = {
                            Log.d(VIDEO_DETAILS_FRAGMENT_TAG, "Video Loading")
                        },
                        onVideoReady = {
                            Log.d(VIDEO_DETAILS_FRAGMENT_TAG, "Video Ready")
                        },
                        onDeleteButtonClicked = {
                            viewModel.deleteProject(checkNotNull(projectId))
                        },
                        onShareButtonClicked = {
                            viewModel.shareProject(checkNotNull(projectId))
                        }
                    )

                    VideoDetailsViewModel.State.ProjectDeleted -> requireActivity().supportFragmentManager.popBackStack()
                    is VideoDetailsViewModel.State.SharingReady -> requireActivity().startActivity(
                        Intent.createChooser(
                            (state.value as VideoDetailsViewModel.State.SharingReady).intent,
                            "Share Video"
                        )
                    )
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val projectId = arguments?.getLong(VIDEO_DETAILS_FRAGMENT_TAG)
        viewModel.loadProject(checkNotNull(projectId))
    }

    override fun onDestroy() {
        super.onDestroy()
        videoDetailsFragmentBinding = null
    }

    companion object {
        val VIDEO_DETAILS_FRAGMENT_TAG = VideoDetailsFragment::class.java.simpleName
        fun newInstance(projectId: Long): VideoDetailsFragment {
            val fragment = VideoDetailsFragment().apply {
                arguments = bundleOf(Pair(VIDEO_DETAILS_FRAGMENT_TAG, projectId))
            }
            return fragment
        }
    }
}
