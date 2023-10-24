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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slot.twostepverification.R
import com.slot.twostepverification.TwoApplication.Companion.localeState
import com.slot.twostepverification.const.locale
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
    val titleStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal)
    val ubTitleStyle = TextStyle(fontSize = 14.sp)
    val configUiState by viewModel.uiState.collectAsStateWithLifecycle()
    var securityChecked by remember { mutableStateOf(false) }
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
                    text = localeState.value.getValue("appearance"),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            ListItem(
                modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                headlineContent = {
                    Text(
                        text = localeState.value.getValue("dynamic_color"),
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        text = localeState.value.getValue("follow_system_desktop_for_theme_color"),
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
                            text = localeState.value.getValue("select_color"),
                            style = titleStyle
                        )
                    },
                    supportingContent = {
                        Text(
                            text = localeState.value.getValue("manually_select_a_color_as_seed"),
                            style = ubTitleStyle
                        )
                    },
                )
            }

            configItem(
                title = localeState.value.getValue("Switch_Language"),
                ubTitle = localeState.value.getValue("The_default_setting_is_usually_fine")
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
                    text = localeState.value.getValue("security_authentication"),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            ListItem(
                modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                headlineContent = {
                    Text(
                        text = localeState.value.getValue("security_authentication"),
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        text = localeState.value.getValue("perform_security_verification_on_startup"),
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
            configItem(
                title = localeState.value.getValue("backup_and_restore"),
                ubTitle = localeState.value.getValue("data_cloud_backup_to_reduce_risk_of_accidental_loss")
            ) { }
            Card(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .paddingFromBaseline(top = 16.dp),
                elevation = 0.dp
            ) {
                Text(
                    text = localeState.value.getValue("About"),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            configItem(
                title = localeState.value.getValue("open_source_license"),
                ubTitle = localeState.value.getValue("no_them_no_me")
            ) { onNavigateToLibs() }
            configItem(
                title = localeState.value.getValue("project_homepage"),
                ubTitle = localeState.value.getValue("View_Source_Code_and_find_job")
            ) { viewModel.openGithub(ctx = ctx) }

            if (configUiState.openThemeDialog) {
                ThemeDialog(
                    dialogTitle = localeState.value.getValue("select_color"),
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