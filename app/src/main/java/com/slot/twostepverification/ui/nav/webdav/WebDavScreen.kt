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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.ui.components.LoadingContent
import com.slot.twostepverification.utils.widget.CtrTextField

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebDavView(
    viewModel: WebDavViewModel = viewModel(),
    onPopBackStack: () -> Unit = {},
    onPopBackStackToNav: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    SideEffect() {
        if (uiState.isLogin) {
            onPopBackStackToNav()
        }
    }

    LaunchedEffect(uiState.message, snackbarHostState) {
        if (uiState.message.isNotBlank()) {
            snackbarHostState.showSnackbar(uiState.message)
            viewModel.resetMessage()
        }
    }
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
                        text = locale("WebDav Config"),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.loginWebDav()
                },
                icon = { Icon(Icons.Filled.AddLink, "Localized Description") },
                text = { Text(text = locale("Login")) },
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data
                )
            }
        },
        content = { innerPadding ->
            LoadingContent(
                isLoading = uiState.isLoading,
                modifier = Modifier.padding(innerPadding)
            ) {
                Column {
                    // webDav地址：默认坚果云
                    CtrTextField(
                        value = LocalConfig.webDavUrl,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp)
                            .fillMaxWidth(),
                        label = locale("Link"),
                        onValueChange = {
                            viewModel.updateDomain(domain = it)
                        }
                    )
                    // WEBDAV 用户名
                    CtrTextField(
                       value = LocalConfig.user,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp)
                            .fillMaxWidth(),
                        label = locale("UserName"),
                        onValueChange = {
                            viewModel.updateUser(user = it)
                        }
                    )
                    CtrTextField(
                        value = LocalConfig.password,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp)
                            .fillMaxWidth(),
                        label = locale("Password"),
                        onValueChange = {
                            viewModel.updatePassword(it)
                        },
                        visualTransformation = PasswordVisualTransformation(mask = '\u2022')
                    )
                }
            }
        }
    )
}
