package com.slot.twostepverification.ui.nav.webdav

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.slot.twostepverification.utils.widget.CtrTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebDavView(
    onPopBackStack: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        onPopBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                title = {
                    Text(
                        text = "配置WebDav",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {},
                icon = { Icon(Icons.Filled.AddLink, "Localized Description") },
                text = { Text(text = "Login") },
            )
        }

    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CtrTextField(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp).fillMaxWidth(),
                label = "链接地址",
                onValueChange = {}
            )
            CtrTextField(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp).fillMaxWidth(),
                label = "用户名",
                onValueChange = {}
            )
            CtrTextField(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp).fillMaxWidth(),
                label = "密码",
                onValueChange = {}
            )
        }
    }
}