package com.slot.twostepverification.ui.nav

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slot.twostepverification.const.LOCALE
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.const.titleStyle
import com.slot.twostepverification.const.ubTitleStyle
import com.slot.twostepverification.utils.camera.utils.PermissionClickView
import com.slot.twostepverification.utils.camera.utils.PermissionView
import com.slot.twostepverification.utils.camera.utils.StorePermissionTextProvider
import com.slot.twostepverification.utils.permission.Permissions

/**
 *  数据备份页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavScreen(
    viewModel: NavViewModel = viewModel(),
    onNavigateToWebDav: () -> Unit,
    onPopBackStack: () -> Unit,
) {
    val navUiState by viewModel.uiState.collectAsStateWithLifecycle()
    // 手动选择备份目录
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
        viewModel.selectFilePath(it)
    }
    val ctx = LocalContext.current


    if (navUiState.isSelectRestoreFileFromWebDav) {
        Dialog(
            onDismissRequest = {
                viewModel.closeSelectRestoreFileFromWebDavDialog()
            }
        ) {
            Scaffold(
                // todo：调整高度
                modifier = Modifier.systemBarsPadding(),
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
                        ),
                        title = {
                            Text(
                                text = locale("select restore file"),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                    )
                }
            ) { innerPadding ->
                Box {
                    LazyColumn(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(navUiState.webDavFileList.size) { index ->
                            val name = navUiState.webDavFileList[index]
                            Card(
                                shape = CardDefaults.outlinedShape,
                                modifier = Modifier
                                    .clickable(onClick = {
                                        //恢复
                                        viewModel.restoreWebDav(name = name)
                                    })
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                ),
                            ) {
                                Text(
                                    text = name,
                                    modifier = Modifier.padding(
                                        start = 15.dp,
                                        top = 18.dp,
                                        bottom = 18.dp
                                    ),
                                    style = TextStyle(
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.W400
                                    ),
                                )
                                Divider(
                                    thickness = 0.3.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3F)
                                )
                            }
                        }
                    }
                    if (navUiState.isShowLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    }
                }
            }
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
                        text = locale("backup_and_restore"),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .paddingFromBaseline(top = 16.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    modifier = Modifier.background(color = Color.White),
                    text = locale("Account"),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // 云链接类型
            ListItem(
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                    .clickable { },
                headlineContent = {
                    Text(
                        text = locale("cloud_connection_type"),
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        text = locale("current_only_support_webdav_sorry"),
                        style = ubTitleStyle
                    )
                }
            )
            // 登录账户
            ListItem(
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                    .clickable {
                        onNavigateToWebDav()
                    },
                headlineContent = {
                    Text(
                        text = locale("Login_Account"),
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        text = if (LocalConfig.isWebDavLogin) "Login:${LocalConfig.user}" else locale(
                            "You_may_need_a_reliable_network_connection"
                        ),
                        style = ubTitleStyle
                    )
                }
            )
            //当前存储于
            ListItem(
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                    .clickable {
                        launcher.launch(null)
                    },
                headlineContent = {
                    Text(
                        text = locale("current_storage_location"),
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        text = locale("Current_storage_path").plus("${navUiState.filePath}"),
                        style = ubTitleStyle
                    )
                }
            )

            Divider(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                thickness = 1.dp
            )
            ListItem(
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                headlineContent = { Text(locale("Notice_RSA")) },
                leadingContent = {
                    Icon(
                        Icons.Filled.HelpOutline,
                        contentDescription = "Localized description",
                    )
                }
            )
            Card(
                modifier = Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp)
                    .paddingFromBaseline(top = 16.dp),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    modifier = Modifier.background(color = Color.White),
                    text = locale("Local"),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // 开始备份
            ListItem(
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                    .clickable {
                        viewModel.backUp()
                    },
                headlineContent = {
                    Text(
                        text = locale("backup"),
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        text = locale("Notice_only_app_itself"),
                        style = ubTitleStyle
                    )
                }
            )


            // 恢复备份
            ListItem(
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                    .clickable {
                        viewModel.selectRestoreFileFromWebDav()
                    },
                headlineContent = {
                    Text(
                        text = locale("Restore_the_backup"),
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        text = locale("restoreFormWebDav"),
                        style = ubTitleStyle
                    )
                }
            )
            // 开始同步
            ListItem(
                modifier = Modifier
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                    .clickable {
                        viewModel.syncWebDav()
                    },
                headlineContent = {
                    Text(
                        text = locale("Sync"),
                        style = titleStyle
                    )
                },
                supportingContent = {
                    Text(
                        text = locale("SyncFromLastBackup"),
                        style = ubTitleStyle
                    )
                }
            )
        }
    }
}