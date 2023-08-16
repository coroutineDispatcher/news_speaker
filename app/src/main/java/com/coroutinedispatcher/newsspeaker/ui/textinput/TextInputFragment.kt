package com.coroutinedispatcher.newsspeaker.ui.textinput

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction.Companion.Next
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.coroutinedispatcher.newsspeaker.MainViewModel
import com.coroutinedispatcher.newsspeaker.R
import com.coroutinedispatcher.newsspeaker.databinding.FragmentTextInputBinding
import com.coroutinedispatcher.newsspeaker.theme.AppTheme
import com.coroutinedispatcher.newsspeaker.ui.camera.CameraFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextInputFragment : Fragment() {

    private var textInputBinding: FragmentTextInputBinding? = null
    private val textInputViewModel by viewModels<TextInputViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.createNewProject()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        textInputBinding = FragmentTextInputBinding.inflate(inflater, container, false)

        requireNotNull(textInputBinding).textInputComposable.setContent {
            val initialTitle = mainViewModel.currentProject?.title.orEmpty()
            val initialContent = mainViewModel.currentProject?.content.orEmpty()

            Log.d(TAG, "TextFragment: $initialTitle")
            Log.d(TAG, "TextFragment: $initialContent")

            val titleText = rememberSaveable { mutableStateOf(initialTitle) }
            val contentText = rememberSaveable { mutableStateOf(initialContent) }

            AppTheme {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
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
                            titleText.value = newValue
                        },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(imeAction = Next),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                mainViewModel.updateTitle(title = titleText.value)
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
                            contentText.value = newValue
                        },
                        keyboardOptions = KeyboardOptions(imeAction = Next),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                mainViewModel.updateContent(content = contentText.value)
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

                                mainViewModel.updateContent(content = titleText.value)
                                mainViewModel.updateTitle(title = titleText.value)

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
            .setTitle("Title missing")
            .setMessage(
                "There is not title set for this project. Please set a title to continue" +
                    " recording."
            )
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showContentBlockerDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle("Content missing")
            .setMessage(
                "There is not title set for this project. Please set a title to continue" +
                    " recording."
            )
            .setPositiveButton("OK") { dialog, _ ->
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
