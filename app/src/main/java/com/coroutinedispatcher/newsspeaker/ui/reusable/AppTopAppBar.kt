package com.coroutinedispatcher.newsspeaker.ui.reusable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    modifier: Modifier,
    appBarMessage: String,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Row(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = modifier
                        .fillMaxWidth(),
                    text = appBarMessage,
                    textAlign = TextAlign.Center
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}
