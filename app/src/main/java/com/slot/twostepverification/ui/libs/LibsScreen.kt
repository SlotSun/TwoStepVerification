package com.slot.twostepverification.ui.libs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.slot.twostepverification.R
import com.slot.twostepverification.const.languageEN
import com.slot.twostepverification.const.locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibsScreen(
    onNavigateToLibDetail: (title:String) -> Unit,
    onPopBackStackToConfig: () -> Unit,
) {
    val ctx = LocalContext.current
    // 开源库
    val libs = listOf(
       "otp","okHttp", "room", "Navigation", "clipboard","otp","okHttp", "room", "Navigation", "clipboard","otp","okHttp", "room", "Navigation", "clipboard"
    )
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        onPopBackStackToConfig()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                title = {
                    Text(text = locale("Third_Party_Disclaimer"), maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(libs.size) { index ->
                Card(
                    shape = CardDefaults.outlinedShape,
                    modifier = Modifier
                        .clickable(onClick = {
                            //跳转去详情页
                            onNavigateToLibDetail(libs[index])
                        })
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                ) {
                    Text(
                        "${libs[index]}",
                        modifier = Modifier.padding(start = 15.dp, top = 18.dp, bottom = 18.dp),
                        style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.W400),
                    )
                    Divider(
                        thickness = 0.3.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3F)
                    )
                }
            }
        }
    }
}


