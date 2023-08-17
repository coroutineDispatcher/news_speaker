package com.coroutinedispatcher.newsspeaker.ui.reusable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    modifier: Modifier,
    appBarMessage: String,
    state: TopAppBarState
) {
    TopAppBar(
        modifier = modifier.padding(8.dp),
        title = {
            Row(modifier = modifier.fillMaxWidth()) {
                Text(
                    modifier = modifier.fillMaxWidth(),
                    text = appBarMessage,
                    textAlign = TextAlign.Center
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
            state = state
        )
    )
}
