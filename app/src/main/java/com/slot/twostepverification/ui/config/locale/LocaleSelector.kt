package com.slot.twostepverification.ui.config.locale

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slot.twostepverification.const.LOCALE
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.const.locales
import com.slot.twostepverification.utils.data.DataStoreUtils

@Composable
fun localeSelector(
    onDismissRequest: () -> Unit,
    changeLocale: (locale: Map<String, String>, key: String) -> Unit,
) {
    val ctx: Context = LocalContext.current
    val options: List<String> = locales.map {
        it.key
    }
    /// todo:需要优化
    var selectedOption by remember { mutableStateOf(DataStoreUtils.getSyncData(LOCALE, "English")) }
    AlertDialog(
        title = {
            Text(
                text = locale("Switch_Language"),
                style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Normal),
            )
        },
        text = {
            // 单选框
            Column {
                // from /const/language
                locales.forEach { (key, v) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = key == selectedOption,
                            onClick = {
                                selectedOption = key
                                changeLocale(v, key)
                            }, colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary,
                            )
                        )

                        Text(
                            text = key,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal),
                        )
                    }
                }
            }

        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(
                    text = locale("ok"),
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal)
                )
            }
        },
    )
}

