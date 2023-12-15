package com.slot.twostepverification.ui.code

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.utils.widget.CtrTextField
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState

enum class VerifyType {
    totp,
    hotp
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalToolbarApi::class)
@Composable
fun CodeView(
    viewModel: CodeViewModel = viewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val pic = "https://slotsun.github.io/img/TwoStep/img1.jpeg"
    val title = locale("Add_New_Item")
    val state = rememberCollapsingToolbarScaffoldState()


    CollapsingToolbarScaffold(
        modifier = Modifier
            .navigationBarsPadding()
            .imeNestedScroll(),
        state = state,
        scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
        toolbar = {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.surface) {
                MyToolBar(state, title, pic, 48.dp, 220.dp, onNavigateBack)
            }
        },
    ) {
        // todo:切换按钮+输入信息
        SwitchButton()
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp) // normal 16dp of padding for FABs
                .navigationBarsPadding() // padding for navigation bar
                .imePadding(), // padding for when IME appears
            onClick = {
                if(viewModel.submit()) {
                    onNavigateBack()
                }
            }
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
        }

    }
}


/**
 * @param collapseHeight  toolbar收缩的高度
 * @param expandHeight    toolbar展开的高度
 */
@Composable
fun CollapsingToolbarScope.MyToolBar(
    state: CollapsingToolbarScaffoldState,
    title: String,
    pic: String,
    collapseHeight: Dp,
    expandHeight: Dp,
    onNavigateBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(expandHeight)
            .pin()//表示固定那里不跟随toolbar变化
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(collapseHeight)
            .pin()
    ) {
        IconButton(
            onClick = {
                onNavigateBack()
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 5.dp),
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIos,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }

    //收缩state.toolbarState.progress为0 ，展开为1
    val progress = state.toolbarState.progress
    //38 ：Toolbar收缩后image的大小，110是Toolbar展开后image的大小
    val imageSize = (38 + (110 - 38) * progress).dp
    //40 ：Toolbar收缩后image x轴的偏移量，10：Toolbar展开后image x轴的偏移量
    val imageOffsetX = (40 + (10 - 40) * progress).dp
    //0 ：Toolbar收缩后image y轴的偏移量，64：Toolbar展开后image y轴的偏移量
    val imageOffsetY = (0 + (64 - 0) * progress).dp

    Surface(
        elevation = 2.dp, color = Color.Transparent, shape = RoundedCornerShape(6.dp),
        modifier = Modifier
            .padding(5.dp)
            .size(imageSize)
            .offset(x = imageOffsetX, y = imageOffsetY)
    ) {
        AsyncImage(
            model = pic,
            contentScale = ContentScale.FillHeight,
            contentDescription = null,
        )
    }

    //90 ：Toolbar收缩后Text X轴的偏移量，135：Toolbar展开后Text X轴的偏移量
    val offsetTextX = (90 + (135 - 90) * progress).dp
    Text(
        text = title,
        modifier = Modifier
            .padding(start = offsetTextX)
            .fillMaxWidth()
            .height(if (progress < 0.2f) collapseHeight else imageSize)
            .wrapContentSize(if (progress < 0.2f) Alignment.CenterStart else Alignment.Center)
            .offset(y = imageOffsetY),
        maxLines = if (progress < 0.2f) 1 else 2,
        overflow = TextOverflow.Ellipsis,
        fontSize = if (progress < 0.2f) 18.sp else 28.sp,
        style = TextStyle(color = Color.Black, textAlign = TextAlign.Left),
    )
}

@Composable
fun SwitchButton() {
    var selectType by remember { mutableStateOf(VerifyType.totp) }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        Row {
            VerifyType.entries.forEach {
                Button(
                    modifier = Modifier.weight(0.5F),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectType == it) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    onClick = {
                        selectType = it

                    },
                ) {
                    Text(text = it.name)
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        OtpView(selectType)

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpView(
    type: VerifyType = VerifyType.totp, viewModel: CodeViewModel = viewModel(),
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("SHA1", "SHA128", "SHA256")
    var selectedOptionText by remember { mutableStateOf(options[0]) }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val nameTxController by viewModel.nameTextFieldController.collectAsStateWithLifecycle()
    val vindorTxController by viewModel.vindorTextFieldController.collectAsStateWithLifecycle()
    val keyTxController by viewModel.keyTextFieldController.collectAsStateWithLifecycle()
    val timeTxController by viewModel.timeTextFieldController.collectAsStateWithLifecycle()

    // 名称
    CtrTextField(
        value = uiState.name,
        label = locale("Name"),
        controller = nameTxController,
        onValueChange = {
            uiState.name = it
        }
    )
    // 服务提供者
    CtrTextField(
        value = uiState.vindor,
        label = locale("Service_Provider"),
        controller = vindorTxController,
        onValueChange = {
            uiState.vindor = it
        }
    )
    // 访问密匙
    CtrTextField(
        value = uiState.key,
        label = locale("Access_Key"),
        enable = uiState.edit,
        controller = keyTxController,
        onValueChange = { uiState.key = it },
    )
    Row {

        if (type == VerifyType.totp) {
            var value by remember { mutableStateOf(uiState.time.toString()) }
            CtrTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(0.5f),
                value = value,
                label = locale("Time_Interval"),
                enable = uiState.edit,
                controller = timeTxController,
                onValueChange = {
                    // 对数据进行过滤
                    val str = it.filter { it.isDigit() }
                    value = str
                    uiState.time = if (str != "") str.toInt() else 0 //debug:删除为空
                },
            )
        } else {
            CtrTextField(
                modifier = Modifier
                    .padding(10.dp)
                    .weight(0.5f),
                value = uiState.count.toString(),
                label = locale("Counter"),
                enable = uiState.edit,
                controller = timeTxController,
                onValueChange = {
                    // 对数据进行过滤
                    val str = it.filter { it.isDigit() }
                    uiState.count = if (str != "") str.toInt() else 0 //debug:删除为空
                },
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .padding(10.dp)
                .weight(0.5f),
            value = uiState.digits.toString(),
            label = { Text(locale("Digits")) },
            onValueChange = { uiState.digits = it.filter { it.isDigit() }.toInt() },
            maxLines = 1,
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        // 下拉框
        ExposedDropdownMenuBox(
            modifier = Modifier
                .padding(10.dp)
                .weight(0.5f),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                // The `menuAnchor` modifier must be passed to the text field for correctness.
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = selectedOptionText,
                onValueChange = {
                    uiState.sha = selectedOptionText
                },
                label = { Text(locale("Hash_Function")) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                options.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption) },
                        onClick = {
                            selectedOptionText = selectionOption
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .padding(10.dp)
                .weight(0.5f)
        )
    }
//    Spacer(
//        modifier = Modifier.windowInsetsBottomHeight(
//            WindowInsets.systemBars
//        )
//    )
}
