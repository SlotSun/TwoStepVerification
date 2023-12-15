package com.slot.twostepverification.utils.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class TextFieldController(
    var isError: Boolean = false,
    var supportingText: String = "",
    var errorMessage: String = "",
)


/**
 *  带控制器的TextField
 *  @param label TextField的标签
 *  @param controller TextField的控制器
 *  @param onValueChange 当TextField输入数值变化时进行操作
 *  @param visualTransformation 是否密码化输入文本
 */
@Composable
fun CtrTextField(
    modifier: Modifier = Modifier.padding(10.dp).fillMaxWidth(),
    value: String = "",
    label: String = "",
    enable:Boolean = true,
    controller: TextFieldController = TextFieldController(),
    onValueChange: (value: String) -> Unit,
    onKeyboardActions:()->Unit ={},
    visualTransformation:VisualTransformation = VisualTransformation.None,

) {
    var value by remember { mutableStateOf(value = value) }
    OutlinedTextField(
        visualTransformation = visualTransformation,
        modifier = modifier,

        value = value,
        label = { Text(text = if (controller.isError) "$label*" else label) },
        onValueChange = {
            value = it
            onValueChange(value)
        },
        enabled = enable,
        maxLines = 1,
        isError = controller.isError,
        keyboardActions = KeyboardActions{
            onKeyboardActions
        },
        supportingText = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                // 错误提示
                text = if (controller.isError) controller.errorMessage else controller.supportingText,
                textAlign = TextAlign.Start,
            )
        }
    )

}