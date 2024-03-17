package com.samadtch.bilinguai.ui.screens.boarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle

data class HypeMessage(
    val start: String,
    val word: List<String>,
    val end: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoardingScreen(
    viewModel: BoardingViewModel,
    splashScreenDone: () -> Unit,
    goHome: () -> Unit,
    goRegister: () -> Unit,
    hypes: List<HypeMessage>
) {
    //------------------------------- Declarations
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { hypes.size })
    var ltr by remember { mutableStateOf(true) }
    var word by remember { mutableStateOf("") }
    val annotatedString = buildAnnotatedString {
        append(hypes[pagerState.currentPage].start)
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append(word) }
        append(hypes[pagerState.currentPage].end)
    }

    //------------------------------- Effects
    LaunchedEffect(uiState) {
        if (uiState != null) {
            if (uiState?.isLoggedIn == true) goHome()
            else if (uiState?.isFirstTime == false) goRegister()
            splashScreenDone()
        }
    }
    LaunchedEffect(pagerState.currentPage) {
        word = ""
        delay(300)//Start
        var notLast: Boolean
        while (true) {
            hypes[pagerState.currentPage].word.forEach { hypeWord ->
                notLast = true
                while (notLast) {
                    if (word.length == hypeWord.length) ltr = false
                    else if (word.isEmpty()) ltr = true
                    if (ltr) word += hypeWord[word.length]
                    else {
                        word = word.dropLast(1)
                        if (word.isEmpty()) notLast = false
                    }
                    delay(300)
                }
            }
        }
    }

    //------------------------------- UI
    Surface(color = MaterialTheme.colorScheme.tertiary) {
        Column {
            HorizontalPager(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.tertiary),
                state = pagerState
            ) {
                Row(Modifier.fillMaxHeight()) {
                    Text(
                        text = annotatedString,
                        modifier = Modifier.align(Alignment.CenterVertically)
                            .padding(horizontal = 16.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 48.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
            Row(
                Modifier.height(32.dp)
                    .background(color = MaterialTheme.colorScheme.tertiary)
                    .align(Alignment.CenterHorizontally)
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .border(
                                color = MaterialTheme.colorScheme.primary,
                                width = 1.dp,
                                shape = CircleShape
                            )
                            .background(
                                if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            ).size(12.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .height(256.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary),
                verticalArrangement = Arrangement.Center
            ) {
                FilledIconButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally).size(96.dp),
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                    onClick = {
                        if (pagerState.currentPage < hypes.size - 1) coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else goRegister()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                        contentDescription = null
                    )
                }
            }
        }
    }
}