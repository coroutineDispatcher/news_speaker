package com.coroutinedispatcher.newsspeaker.ui.textinput

import android.app.AlertDialog
import android.os.Bundle
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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction.Companion.Next
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.coroutinedispatcher.newsspeaker.R
import com.coroutinedispatcher.newsspeaker.databinding.FragmentTextInputBinding
import com.coroutinedispatcher.newsspeaker.ui.camera.CameraFragment
import com.coroutinedispatcher.newsspeaker.ui.theme.NewsSpeakerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextInputFragment : Fragment() {

    private var textInputBinding: FragmentTextInputBinding? = null
    private val textInputViewModel by viewModels<TextInputViewModel>()
    private var currentProjectId: Long = -1

    override fun onResume() {
        super.onResume()
        textInputViewModel.getOrCreate(currentProjectId)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        textInputBinding = FragmentTextInputBinding.inflate(inflater, container, false)

        requireNotNull(textInputBinding).textInputComposable.setContent {
            val textInputUIState = textInputViewModel.state.collectAsStateWithLifecycle()
            val titleText = remember { textInputViewModel.titleText }
            val contentText = remember { textInputViewModel.contentText }

            contentText.value = textInputUIState.value.project?.content.orEmpty()
            titleText.value = textInputUIState.value.project?.title.orEmpty()

            currentProjectId = textInputUIState.value.project?.pId ?: -1

            NewsSpeakerTheme(activityContext = requireActivity()) {
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
                                textInputViewModel.update(
                                    title = titleText.value,
                                    content = contentText.value
                                )
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
                                textInputViewModel.update(
                                    content = contentText.value,
                                    title = titleText.value
                                )
                            }
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp)
                    ) {
                        ElevatedButton(
                            onClick = {
                                if (titleText.value.isEmpty() || titleText.value.isBlank()) {
                                    showTitleBlockerDialog()
                                    return@ElevatedButton
                                }
                                if (contentText.value.isEmpty() || contentText.value.isBlank()) {
                                    showContentBlockerDialog()
                                    return@ElevatedButton
                                }

                                textInputViewModel.update(
                                    title = titleText.value,
                                    content = contentText.value
                                )

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

        currentProjectId = arguments?.getLong(PROJECT_ID_FRAGMENT_ARG) ?: -1
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
        const val PROJECT_ID_FRAGMENT_ARG = "project_id_fragment_arg"
        fun newInstance(projectId: Long = -1): TextInputFragment {
            val fragment = TextInputFragment()
            val arguments = Bundle()
            arguments.putLong(PROJECT_ID_FRAGMENT_ARG, projectId)
            fragment.arguments = arguments
            return fragment
        }

        const val TAG = "TextInputFragment"
    }
}
