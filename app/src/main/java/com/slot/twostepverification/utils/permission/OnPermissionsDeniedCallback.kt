package com.slot.twostepverification.utils.permission

interface OnPermissionsDeniedCallback {

    fun onPermissionsDenied(deniedPermissions: Array<String>)

}
