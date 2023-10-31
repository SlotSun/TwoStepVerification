package com.slot.twostepverification.ui.home

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.slot.twostepverification.R
import com.slot.twostepverification.const.TOTP_TIME
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.ui.home.components.ListItemView

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToConfig: () -> Unit = {},
    onNavigateToScan: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val homeListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val beginTime by remember { mutableFloatStateOf(0.1f) }
    //确定当前进度条:不懂为什么不动
    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 30000,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "",
    )
    val transition = rememberInfiniteTransition(label = "")
    val animateFlot = transition.animateFloat(
        initialValue = beginTime, targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(
            tween(durationMillis = TOTP_TIME, easing = LinearEasing)
        ),
        label = "item",
    )
    val itemList by remember { mutableStateOf(uiState.listItem) }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by remember { mutableStateOf(false) }
    var edgeToEdgeEnabled by remember { mutableStateOf(false) }
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
        floatingActionButton = {
            Button(
                onClick = {
                    openBottomSheet = !openBottomSheet
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
        Column(modifier = Modifier.padding(innerPadding)) {
            uiState.listItem.forEach() {
                ListItemView(item = it)
            }
        }
    }
    if (openBottomSheet) {
        val windowInsets =
            if (edgeToEdgeEnabled) WindowInsets(0) else BottomSheetDefaults.windowInsets
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState,
            windowInsets = windowInsets,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                Card(
                    onClick = {
                        openBottomSheet = !openBottomSheet
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
                Card(
                    onClick = {
                        Toast.makeText(context, "11", Toast.LENGTH_SHORT).show()
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
                Card(
                    onClick = {
                        Toast.makeText(context, "11", Toast.LENGTH_SHORT).show()
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
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        }
    }
}