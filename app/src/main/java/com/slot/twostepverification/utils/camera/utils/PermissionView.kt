package com.slot.twostepverification.utils.camera.utils


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.slot.twostepverification.const.locale
import com.slot.twostepverification.utils.permission.Permissions
import com.slot.twostepverification.utils.permission.PermissionsCompat


/*
 * 权限请求
 * 
 * @author: Slot
 * @date: 2023/11/01
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionView(
    viewModel: PermissionViewModel = viewModel(),
    permission: String = Permissions.CAMERAS,
    rationale: String = locale("PermissionNeed"),
    content: @Composable () -> Unit = { }
) {
    val permissionState = rememberPermissionState(permission = permission)
    val ctx = LocalContext.current as Activity

    val dialogQueue = viewModel.permissionDialogQueue
    val singlePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(permission = permission, isGranted = isGranted)
        }
    )
    LaunchedEffect(viewModel) {
        if (!permissionState.status.isGranted) {
            singlePermissionResultLauncher.launch(permission)
        }
    }
    fun openAppSettings() {
        ctx.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", ctx.packageName, null)
            }
        )
        ctx.startActivityForResult(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", ctx.packageName, null)
            },1
        )
    }
    // dialog 有需要申请的权限就弹出请求
    dialogQueue
        .reversed()//反转
        .forEach { permissionItem ->
            PermissionDialog(
                permissionTextProvider = when (permissionItem) {
                    permission -> {
                        CameraPermissionTextProvider()
                    }
                    else -> return@forEach
                },
                //请看底部shouldShowRequestPermissionRationale的详解
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(ctx, permissionItem),
                onDismiss = viewModel::dismissDialog,
                onOkClick = {
                    //取消当前Dialog，再次向用户申请
                    viewModel.dismissDialog()
                    //singlePermissionResultLauncher.launch(Manifest.permission.CAMERA)
                    singlePermissionResultLauncher.launch(
                        permissionItem
                    )
                },
                onGoToAppSettings = ::openAppSettings
            )
        }
    content()
}

@Composable
fun PermissionDialog(
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = locale("PermissionPlease"))
        },
        text = {
            Text(permissionTextProvider.getDescription(isPermanentlyDeclined = isPermanentlyDeclined))
        },
        confirmButton = {
            TextButton(onClick = {
                if (isPermanentlyDeclined) {
                    onGoToAppSettings()
                } else {
                    onOkClick()
                }
            }) {
                Text(if (isPermanentlyDeclined) "授予权限" else locale("OK"))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
            }) {
                Text(locale("cancel"))
            }
        }
    )

}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

//这里通过类的方式提供文本
class CameraPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "您好像永久的拒绝了相机权限，您可以转到应用程序设置授予相机权限"
        } else {
            "我们需要访问你的相机，以便你可以在App使用相机功能"
        }
    }
}
