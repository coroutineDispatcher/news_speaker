package com.coroutinedispatcher.newsspeaker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coroutinedispatcher.newsspeaker.ImageThumbnail
import com.coroutinedispatcher.newsspeaker.R
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.databinding.FragmentMainBinding
import com.coroutinedispatcher.newsspeaker.theme.AppTheme
import com.coroutinedispatcher.newsspeaker.ui.reusable.EmptyScreen
import com.coroutinedispatcher.newsspeaker.ui.textinput.TextInputFragment
import com.coroutinedispatcher.newsspeaker.ui.videodetails.VideoDetailsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel by viewModels<MainViewModel>()
    private var mainFragmentBinding: FragmentMainBinding? = null
    private val binding
        get() = checkNotNull(mainFragmentBinding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainFragmentBinding = FragmentMainBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            val state = viewModel.state.collectAsStateWithLifecycle(MainViewModel.State.Loading)

            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            when (state.value) {
                                MainViewModel.State.Empty -> EmptyScreen()
                                is MainViewModel.State.Success -> Projects(
                                    (state.value as MainViewModel.State.Success).data,
                                    onItemClicked = { projectId ->
                                        requireActivity().supportFragmentManager.commit {
                                            setCustomAnimations(
                                                R.anim.slide_in,
                                                R.anim.fade_out,
                                                R.anim.fade_in,
                                                R.anim.slide_out
                                            )
                                            addToBackStack(VideoDetailsFragment.VIDEO_DETAILS_FRAGMENT_TAG)
                                            replace(
                                                R.id.container,
                                                VideoDetailsFragment.newInstance(projectId)
                                            )
                                        }
                                    }
                                )

                                MainViewModel.State.Loading -> CircularProgressIndicator()
                            }
                        }

                        ExtendedFloatingActionButton(
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

        return binding.root
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun Projects(data: List<Project>, onItemClicked: (Long) -> Unit) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Fixed(2)
        ) {
            items(data) { project ->
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    ImageThumbnail(project = project, onItemClicked = onItemClicked)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainFragmentBinding = null
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
