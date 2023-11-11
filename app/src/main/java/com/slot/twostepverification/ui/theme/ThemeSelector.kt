package com.slot.twostepverification.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.slot.twostepverification.R
import com.slot.twostepverification.TwoApplication.Companion.localeState
import com.slot.twostepverification.const.CHANGED_THEME
import com.slot.twostepverification.utils.data.DataStoreUtils

data class ThemeModel(val color: Color, val colorId: Int, val colorName: String)

// 主题model列表
private val themeList = arrayListOf(
    ThemeModel(select_theme, SKY_BLUE_THEME, "天蓝色"),
    ThemeModel(gray_theme, GRAY_THEME, "灰色"),
    ThemeModel(green_theme, GREEN_THEME, "绿色"),
    ThemeModel(purple_theme, PURPLE_THEME, "紫色"),
    ThemeModel(orange_theme, ORANGE_THEME, "橘黄色"),
    ThemeModel(cyan_theme, CYAN_THEME, "青色"),
    ThemeModel(magenta_theme, MAGENTA_THEME, "品红色"),
)

@Composable
fun ThemeDialog(
    dialogTitle: String,
    icon: ImageVector,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "selectColor")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            ThemeGrid()
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(text = localeState.value.getValue("OK"))
            }
        },
    )

}


@Composable
fun ThemeGrid() {
    var playTheme by remember { mutableIntStateOf(ORANGE_THEME) }
    LaunchedEffect(Unit) {
        playTheme = DataStoreUtils.getSyncData(CHANGED_THEME, ORANGE_THEME)
    }
    Column(
        modifier = Modifier.padding(10.dp)

    ) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            items(themeList) { item ->
                ThemeItem(playTheme, item) {
                    playTheme = item.colorId
                    themeTypeState.value = playTheme
                    //保存静态
                    DataStoreUtils.putSyncData(CHANGED_THEME, playTheme)
                }
            }
        }
        Text(
            text = localeState.value.getValue("theme_warn"),
            modifier = Modifier
                .padding(15.dp),
            color = Color.Black,
        )

    }
}

@Composable
private fun ThemeItem(playTheme: Int, item: ThemeModel, onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable {
                onClick()
            }
            .padding(10.dp)
    ) {
        val isCurrentTheme = item.colorId == playTheme
        Box(
            modifier = Modifier
                .size(40.dp)
                .shadow(2.dp, shape = MaterialTheme.shapes.medium)
                .background(color = item.color),
            contentAlignment = Alignment.Center,

            ) {
            if (isCurrentTheme) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = "back",
                    tint = select_theme
                )
            }
        }
        Text(
            text = item.colorName,
            color = item.color,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}