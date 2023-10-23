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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
                    Text("设置", maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                Text("外观", color = MaterialTheme.colorScheme.primary)
            }
            ListItem(
                modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                headlineContent = {
                    Text(
                        "动态取色",
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        "跟随系统桌面自动获取主题色",
                        style = ubTitleStyle
                    )
                },
                trailingContent = {
                    Switch(
                        checked = configUiState.dynamicColorChecked,
                        onCheckedChange = {
                            viewModel.setDynamicColor(it)
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
                                viewModel.openDialog()
                            }
                        ),
                    headlineContent = {
                        Text(
                            "选取颜色",
                            style = titleStyle
                        )
                    },
                    supportingContent = {
                        Text(
                            "手动选择一个颜色，这将作为种子被应用",
                            style = ubTitleStyle
                        )
                    },
                )
            }
            configItem("切换语言", "正常来说，默认就好") { }
            Card(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .paddingFromBaseline(top = 16.dp),
                elevation = 0.dp
            ) {
                Text("数据", color = MaterialTheme.colorScheme.primary)
            }
            ListItem(
                modifier = Modifier.padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                headlineContent = {
                    Text(
                        "安全认证",
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        "启动时选择指纹解锁",
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
            configItem("备份和恢复", "数据上云，减少意外丢失风险") { }
            Card(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .paddingFromBaseline(top = 16.dp),
                elevation = 0.dp
            ) {
                Text("关于", color = MaterialTheme.colorScheme.primary)
            }
            configItem("开源许可", "没有他们就没有我☺") { onNavigateToLibs() }
            configItem("项目主页", "看看代码，赏口饭吃") { viewModel.openGithub(ctx = ctx) }

            if (configUiState.openDialog){
                ThemeDialog(
                    dialogTitle = "主题选择器",
                    onDismissRequest = { viewModel.closeDialog() },
                    icon = Icons.Filled.Info
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
                "$title",
                style = titleStyle
            )
        },
        supportingContent = {
            Text(
                "$ubTitle",
                style = ubTitleStyle
            )
        },
    )
}