package com.slot.twostepverification.utils.camera.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.slot.twostepverification.utils.permission.Permissions

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionClickView(
    viewModel: PermissionViewModel = viewModel(),
    permission: String = Permissions.WRITE_EXTERNAL_STORAGE,
    permissionTextProvider: PermissionTextProvider = StorePermissionTextProvider(),
    needPermissionClick:()->Unit = {},
    content: @Composable () -> Unit = { }
) {

    val permissionState = rememberPermissionState(permission = permission)
    val ctx = LocalContext.current as ComponentActivity
    val dialogQueue = viewModel.permissionDialogQueue
    // 单项权限请求器
    val singlePermissionResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.onPermissionResult(permission = permission, isGranted = isGranted)
        }
    )
    // 跳转app详情设置启动器
    val openSettingsResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            // 返回时 再次请求权限
            if (!permissionState.status.isGranted) {
                singlePermissionResultLauncher.launch(permission)
            }
        }
    )
    // dialog 有需要申请的权限就弹出请求
    dialogQueue
        .reversed()//反转
        .forEach { permissionItem ->
            PermissionDialog(
                permissionTextProvider = when (permissionItem) {
                    permission -> {
                        permissionTextProvider
                    }

                    else -> return@forEach
                },
                //shouldShowRequestPermissionRationale 为false 用户永久拒绝了：引导用户去设置里开放权限
                isPermanentlyDeclined = !ActivityCompat.shouldShowRequestPermissionRationale(
                    ctx,
                    permissionItem
                ),
                onDismiss = viewModel::dismissDialog,
                onOkClick = {
                    //取消当前Dialog，再次向用户申请
                    viewModel.dismissDialog()
                    singlePermissionResultLauncher.launch(
                        permissionItem
                    )
                },
                onGoToAppSettings = {
                    viewModel.dismissDialog()
                    openSettingsResultLauncher.launch(
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", ctx.packageName, null)
                        }
                    )
                }
            )
        }
    Surface(
        modifier = Modifier.clickable {
            if (!permissionState.status.isGranted) {
                singlePermissionResultLauncher.launch(permission)
            }else{
                needPermissionClick()
            }
        }
    ) {
        content()
    }

}
