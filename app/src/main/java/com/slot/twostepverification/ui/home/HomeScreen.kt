package com.slot.twostepverification.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slot.twostepverification.R
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.help.TwoHelper
import com.slot.twostepverification.ui.home.components.ListItemView
import com.slot.twostepverification.utils.showToast

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToConfig: () -> Unit = {},
    onNavigateToScan: () -> Unit = {},
    onNavigateToCode: () -> Unit = {}
) {
    val ctx = LocalContext.current
    val uiState by TwoHelper.itemState.collectAsStateWithLifecycle()
    val homeUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val skipPartiallyExpanded by remember { mutableStateOf(false) }
    var openUriSheet by rememberSaveable { mutableStateOf(false) }
    val edgeToEdgeEnabled by remember { mutableStateOf(false) }
    val windowInsets =
        if (edgeToEdgeEnabled) WindowInsets(0) else BottomSheetDefaults.windowInsets

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded
    )


    LaunchedEffect(Unit) {
        viewModel.getListItem()
    }
    SideEffect() {

    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = locale("app_name"), maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                actions = {
                    IconButton(onClick = {
                        onNavigateToConfig()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        },
        // 底部功能弹窗控制
        floatingActionButton = {
            Button(
                onClick = {
                    viewModel.openBottomSheet()
                },
                elevation = ButtonDefaults.elevation(0.dp, 0.dp, 0.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    contentColor = colorResource(R.color.white)
                ),
                contentPadding = PaddingValues(15.dp),
                modifier = Modifier.size(55.dp)
            ) {
                Icon(
                    Icons.Outlined.Add, contentDescription = null
                )
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(scrollState)) {
            uiState.listItem.forEach() {
                ListItemView(item = it)
            }
        }
    }
    // 导入url弹窗
    if (openUriSheet) {
        var value by remember { mutableStateOf("") }
        AlertDialog(
            title = {
                Text(locale("Import_from_URI"))
            },
            text = {
                OutlinedTextField(
                    value = value,
                    label = { Text(locale("URI_Link")) },
                    onValueChange = { value = it },
                    maxLines = 1,
                    modifier = Modifier.padding(10.dp),
                )
            },
            onDismissRequest = {
                openUriSheet = false
            },
            confirmButton = {
                TextButton(onClick = {
                    // 关闭dialog
                    openUriSheet = false
                    val res = viewModel.importUrl(value)
                    showToast(context = ctx, text = res)
                }) {
                    Text(
                        text = locale("Import"),
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal)
                    )
                }
            }
        )
    }
    // 底部功能弹窗
    if (homeUiState.openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                viewModel.closeItemSettings()
            },
            sheetState = bottomSheetState,
            windowInsets = windowInsets,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {

                // itemSetting
                if (homeUiState.openItemSettings) {
                    //删除+修改+设置位置
                    Card(
                        onClick = {
                            viewModel.closeBottomSheet()
                            viewModel.removeListItem()
                        }
                    ) {
                        ListItem(
                            headlineContent = { Text(text = "删除") },
                            leadingContent = {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                    }
                } else {
                    Card(
                        onClick = {
                            viewModel.closeBottomSheet()
                            onNavigateToScan()
                        }
                    ) {
                        ListItem(
                            headlineContent = { Text(text = locale("Scan_QR_Code")) },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.photo_camera),
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                    }
                }

                // 手动输入
                Card(
                    onClick = {
                        if (homeUiState.openItemSettings) {
                            onNavigateToCode()
                        } else {
                            viewModel.initCodeUiState()
                            onNavigateToCode()
                        }
                        viewModel.closeBottomSheet()
                    }
                ) {
                    ListItem(
                        headlineContent = { Text(locale("manual_input")) },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = "Localized description",
                            )
                        }
                    )
                }
                // itemSetting
                if (homeUiState.openItemSettings) {
                    //删除+修改+设置位置
                } else {
                    Card(
                        onClick = {
                            viewModel.closeBottomSheet()
                            openUriSheet = !openUriSheet

                        }
                    ) {
                        ListItem(
                            headlineContent = { Text(locale("parse_uri")) },
                            leadingContent = {
                                Icon(
                                    Icons.Filled.Email,
                                    contentDescription = "Localized description",
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}