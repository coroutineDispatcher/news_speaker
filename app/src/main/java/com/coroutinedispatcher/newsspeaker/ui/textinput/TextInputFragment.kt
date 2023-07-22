package com.coroutinedispatcher.newsspeaker.ui.textinput

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction.Companion.Next
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.coroutinedispatcher.newsspeaker.databinding.FragmentTextInputBinding
import com.coroutinedispatcher.newsspeaker.ui.theme.NewsSpeakerTheme

class TextInputFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTextInputBinding.inflate(inflater, container, false)

        binding.textInputComposable.setContent {
            val titleText = remember { mutableStateOf("") }
            val contentText = remember { mutableStateOf("") }

            NewsSpeakerTheme {
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
                        keyboardOptions = KeyboardOptions(imeAction = Next)
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
                        keyboardOptions = KeyboardOptions(imeAction = Next)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(16.dp)
                    ) {
                        ElevatedButton(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text(text = "Next")
                        }
                    }
                }
            }
        }
        return binding.root
    }

    companion object {
        fun newInstance() = TextInputFragment()
    }
}
