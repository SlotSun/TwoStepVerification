package com.slot.twostepverification.utils.camera.utils


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState
import com.slot.twostepverification.const.locale


/*
 * 权限请求
 * 
 * @author: Slot
 * @date: 2023/11/01
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionView(
    permission: String = android.Manifest.permission.CAMERA,
    rationale: String = locale("PermissionNeed"),
    permissionNotAvailableContent: @Composable () -> Unit = { },
    content: @Composable () -> Unit = { }
) {
    val permissionState = rememberPermissionState(permission)
    PermissionRequired(
        permissionState = permissionState,
        permissionNotGrantedContent = {
            Rationale(
                text = rationale,
                onRequestPermission = { permissionState.launchPermissionRequest() }
            )
        },
        permissionNotAvailableContent = permissionNotAvailableContent,
        content = content
    )
}

@Composable
private fun Rationale(
    text: String,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Don't */ },
        title = {
            Text(text = locale("PermissionPlease"))
        },
        text = {
            Text(text)
        },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text(locale("OK"))
            }
        }
    )
}

fun openSettingsPermission(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    )
}
