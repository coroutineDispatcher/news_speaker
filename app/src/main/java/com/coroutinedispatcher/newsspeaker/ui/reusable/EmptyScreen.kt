package com.coroutinedispatcher.newsspeaker.ui.reusable

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coroutinedispatcher.newsspeaker.R

@Composable
fun EmptyScreen(modifier: Modifier = Modifier) {
    val currentEmptyImage = if (isSystemInDarkTheme()) {
        R.drawable.empty_dark_mode
    } else {
        R.drawable.empty_light_mode
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        EmptyScreenText(
            text = stringResource(id = R.string.nothing_here),
            fontSize = 24.sp,
            paddingValues = PaddingValues(16.dp)
        )
        Image(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(100.dp)
                .height(100.dp),
            painter = painterResource(id = currentEmptyImage),
            contentDescription = stringResource(id = R.string.empty)
        )
        EmptyScreenText(
            text = stringResource(id = R.string.create_new_project),
            fontSize = 18.sp,
            paddingValues = PaddingValues(top = 16.dp, bottom = 2.dp)
        )
        EmptyScreenText(
            text = stringResource(id = R.string.new_projects_stay_here),
            fontSize = 18.sp,
            paddingValues = PaddingValues(top = 2.dp)
        )
    }
}

@Composable
fun ColumnScope.EmptyScreenText(
    modifier: Modifier = Modifier,
    fontSize: TextUnit,
    text: String,
    paddingValues: PaddingValues
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .align(Alignment.CenterHorizontally)
            .padding(paddingValues),
        textAlign = TextAlign.Center,
        text = text,
        fontSize = fontSize
    )
}
