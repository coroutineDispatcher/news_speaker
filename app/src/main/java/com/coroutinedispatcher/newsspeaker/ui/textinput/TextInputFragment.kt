package com.coroutinedispatcher.newsspeaker.ui.textinput

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction.Companion.Next
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.coroutinedispatcher.newsspeaker.MainViewModel
import com.coroutinedispatcher.newsspeaker.R
import com.coroutinedispatcher.newsspeaker.databinding.FragmentTextInputBinding
import com.coroutinedispatcher.newsspeaker.theme.AppTheme
import com.coroutinedispatcher.newsspeaker.ui.camera.CameraFragment
import com.coroutinedispatcher.newsspeaker.ui.reusable.AppTopAppBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextInputFragment : Fragment() {

    private var textInputBinding: FragmentTextInputBinding? = null
    private val binding get() = checkNotNull(textInputBinding)
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.createNewProject()
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.deleteProjectIfEmptyContent()
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        textInputBinding = FragmentTextInputBinding.inflate(inflater, container, false)

        binding.textInputComposable.setContent {
            val initialTitle = mainViewModel.currentProject?.title.orEmpty()
            val initialContent = mainViewModel.currentProject?.content.orEmpty()
            val localFocusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

            Log.d(TAG, "TextFragment: $initialTitle")
            Log.d(TAG, "TextFragment: $initialContent")

            val titleText = rememberSaveable { mutableStateOf(initialTitle) }
            val contentText = rememberSaveable { mutableStateOf(initialContent) }

            DisposableEffect(key1 = Unit, effect = {
                onDispose { }
            })

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
                    }
                ) { paddingValues ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            value = titleText.value,
                            placeholder = {
                                Text(text = "Title")
                            },
                            onValueChange = { newValue ->
                                mainViewModel.updateTitle(newValue)
                                titleText.value = newValue
                            },
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(imeAction = Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    mainViewModel.updateTitle(title = titleText.value)
                                    localFocusManager.moveFocus(FocusDirection.Down)
                                }
                            )
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                                .fillMaxWidth()
                                .weight(1f),
                            value = contentText.value,
                            placeholder = {
                                Text(text = "Content")
                            },
                            onValueChange = { newValue ->
                                mainViewModel.updateContent(newValue)
                                contentText.value = newValue
                            },
                            keyboardOptions = KeyboardOptions(imeAction = Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    mainViewModel.updateContent(content = contentText.value)
                                    localFocusManager.moveFocus(FocusDirection.Down)
                                },
                                onDone = {
                                    localFocusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            )
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (titleText.value.isEmpty() || titleText.value.isBlank()) {
                                        // TODO Update these dialogs
                                        showTitleBlockerDialog()
                                        return@Button
                                    }
                                    if (contentText.value.isEmpty() || contentText.value.isBlank()) {
                                        // TODO Update these dialogs
                                        showContentBlockerDialog()
                                        return@Button
                                    }
                                    switchToCameraFragment()
                                },
                                modifier = Modifier.align(Alignment.BottomEnd)
                            ) {
                                Text(text = "Next")
                            }
                        }
                    }
                }
            }
        }

        return requireNotNull(textInputBinding).root
    }

    private fun switchToCameraFragment() {
        requireActivity().supportFragmentManager.commit {
            setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            addToBackStack(CameraFragment.TAG)
            replace(R.id.container, CameraFragment.newInstance())
        }
    }

    private fun showTitleBlockerDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.title_missin)
            .setMessage(R.string.no_title)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showContentBlockerDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.content_missing)
            .setMessage(R.string.no_content)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        textInputBinding = null
    }

    companion object {
        fun newInstance(): TextInputFragment = TextInputFragment()

        const val TAG = "TextInputFragment"
    }
}
