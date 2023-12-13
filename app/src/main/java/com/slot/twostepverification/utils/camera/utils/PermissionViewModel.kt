package com.slot.twostepverification.utils.camera.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class PermissionViewModel : ViewModel() {

    //权限队列:或许可以用flow实现
    val permissionDialogQueue = mutableStateListOf<String>()

    //取消Dialog
    fun dismissDialog() {
        permissionDialogQueue.removeFirst()
    }

    //根据用户选择的结果进行处理
    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if (!isGranted && !permissionDialogQueue.contains(permission)) {
            //用户不同意则需要加入队列重新向用户申请
            permissionDialogQueue.add(permission)
        }
    }
}
