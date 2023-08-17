package com.coroutinedispatcher.newsspeaker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coroutinedispatcher.newsspeaker.R
import com.coroutinedispatcher.newsspeaker.database.Project
import com.coroutinedispatcher.newsspeaker.databinding.FragmentMainBinding
import com.coroutinedispatcher.newsspeaker.theme.AppTheme
import com.coroutinedispatcher.newsspeaker.ui.reusable.AppTopAppBar
import com.coroutinedispatcher.newsspeaker.ui.reusable.EmptyScreen
import com.coroutinedispatcher.newsspeaker.ui.reusable.ImageThumbnail
import com.coroutinedispatcher.newsspeaker.ui.textinput.TextInputFragment
import com.coroutinedispatcher.newsspeaker.ui.videodetails.VideoDetailsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel by viewModels<HomeViewModel>()
    private var mainFragmentBinding: FragmentMainBinding? = null
    private val binding
        get() = checkNotNull(mainFragmentBinding)

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainFragmentBinding = FragmentMainBinding.inflate(inflater, container, false)

        binding.composeView.setContent {
            val state = viewModel.state.collectAsStateWithLifecycle()
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

            AppTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        AppTopAppBar(
                            modifier = Modifier,
                            state = scrollBehavior.state,
                            appBarMessage = stringResource(id = R.string.app_name)
                        )
                    },
                    content = { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                when (state.value) {
                                    HomeViewModel.State.Empty -> EmptyScreen()
                                    is HomeViewModel.State.Success -> Projects(
                                        (state.value as HomeViewModel.State.Success).data,
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

                                    HomeViewModel.State.Loading -> CircularProgressIndicator()
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
                )
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
                ImageThumbnail(
                    modifier = Modifier.padding(2.dp),
                    project = project,
                    onItemClicked = onItemClicked
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainFragmentBinding = null
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
