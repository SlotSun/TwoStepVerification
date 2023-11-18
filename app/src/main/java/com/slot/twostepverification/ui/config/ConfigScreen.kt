package com.slot.twostepverification.ui.config

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slot.twostepverification.TwoApplication.Companion.localeState
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.const.titleStyle
import com.slot.twostepverification.const.ubTitleStyle
import com.slot.twostepverification.ui.config.locale.localeSelector
import com.slot.twostepverification.ui.theme.ThemeDialog


@OptIn(ExperimentalMaterial3Api::class)
@Preview()
@Composable
fun ConfigScreen(
    viewModel: ConfigViewModel = viewModel(),
    onNavigateToBackup: () -> Unit = {},
    onNavigateToLibs: () -> Unit = {},
    onPopBackStackToMain: () -> Unit = {},
) {
    val ctx = LocalContext.current

    val configUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val securityChecked by remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        onPopBackStackToMain()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                title = {
                    Text(
                        text = locale("settings"),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .paddingFromBaseline(top = 16.dp),
                elevation = 0.dp
            ) {
                Text(
                    text = locale("appearance"),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            ListItem(
                modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                headlineContent = {
                    Text(
                        text = locale("dynamic_color"),
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        text = locale("follow_system_desktop_for_theme_color"),
                        style = ubTitleStyle
                    )
                },
                trailingContent = {
                    Switch(
                        checked = configUiState.dynamicColorChecked,
                        onCheckedChange = {
                            viewModel.setDynamicColor(it, ctx = ctx)
                        }
                    )
                }
            )
            if (!configUiState.dynamicColorChecked) {
                ListItem(
                    modifier = Modifier
                        .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                        .clickable(
                            onClick = {
                                viewModel.openThemeDialog()
                            }
                        ),
                    headlineContent = {
                        Text(
                            text = locale("select_color"),
                            style = titleStyle
                        )
                    },
                    supportingContent = {
                        Text(
                            text = locale("manually_select_a_color_as_seed"),
                            style = ubTitleStyle
                        )
                    },
                )
            }

            configItem(
                title = locale("Switch_Language"),
                ubTitle = locale("The_default_setting_is_usually_fine")
            ) {
                viewModel.openLocaleDialog()
            }
            Card(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .paddingFromBaseline(top = 16.dp),
                elevation = 0.dp
            ) {
                Text(
                    text = locale("security_authentication"),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            ListItem(
                modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                headlineContent = {
                    Text(
                        text = locale("security_authentication"),
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        text = locale("perform_security_verification_on_startup"),
                        style = ubTitleStyle
                    )
                },
                trailingContent = {
                    Switch(
                        checked = securityChecked,
                        onCheckedChange = {

                        }
                    )
                }
            )
            // 跳转备份
            configItem(
                title = locale("backup_and_restore"),
                ubTitle = locale("data_cloud_backup_to_reduce_risk_of_accidental_loss")
            ) {
                onNavigateToBackup()
            }
            Card(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .paddingFromBaseline(top = 16.dp),
                elevation = 0.dp
            ) {
                Text(
                    text = locale("About"),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            configItem(
                title = locale("open_source_license"),
                ubTitle = locale("no_them_no_me")
            ) { onNavigateToLibs() }
            configItem(
                title = locale("project_homepage"),
                ubTitle = locale("View_Source_Code_and_find_job")
            ) { viewModel.openGithub(ctx = ctx) }

            if (configUiState.openThemeDialog) {
                ThemeDialog(
                    dialogTitle = locale("select_color"),
                    onDismissRequest = { viewModel.closeThemeDialog() },
                    icon = Icons.Filled.Info
                )
            }
            if (configUiState.openLocaleDialog) {
                localeSelector(
                    onDismissRequest = { viewModel.closeLocaleDialog() },
                    changeLocale = { v, k ->
                        viewModel.changeLocale(v, k)
                    }
                )
            }
        }

    }
}


@Composable
fun configItem(title: String, ubTitle: String, function: () -> Unit) {
    val titleStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal)
    val ubTitleStyle = TextStyle(fontSize = 14.sp)
    ListItem(
        modifier = Modifier
            .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
            .clickable(
                onClick = {
                    function()
                }
            ),
        headlineContent = {
            Text(
                title,
                style = titleStyle
            )
        },
        supportingContent = {
            Text(
                ubTitle,
                style = ubTitleStyle
            )
        },
    )
}