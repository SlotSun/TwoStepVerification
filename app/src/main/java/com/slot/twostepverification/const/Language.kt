package com.slot.twostepverification.const

import com.slot.twostepverification.const.LocalConfig.localeState
import java.util.Locale

// todo:后续用object 实现
// questions:1-> 如何状态刷新
/**
 *  i18n
 */
data class Language(var locale: Locale = Locale.getDefault()) {
//    private var locale = Locale.getDefault()

    /**
     *  切换当前APP界面语言
     *  @param locale  java.util.Locale
     */
    val login:  String
        get() = when (locale) {
            Locale.CHINA -> "登录"
            Locale.ENGLISH -> "login"
            else -> "login"
        }
    val ok:String
        get() = when(locale) {
            Locale.CHINA -> "确定"
            Locale.ENGLISH -> "ok"
            else -> "ok"
        }
}



//英语
val languageEN: Map<String, String> = mapOf(
    "app_name" to "Two-Factor Authentication",
    "title_activity_main_view" to "MainViewActivity",
    "title_activity_main2" to "MainActivity2",
    "theme_warn" to "Note: Theme switching only takes effect in light mode and dark mode is pure black (for power saving); but there is a special case: that is  if you open the use of system wallpaper related colors, the theme color will be set to be related to the system wallpaper color.",
    "manual_input" to "Manual Input",
    "Scan_QR_Code" to "Scan QR Code",
    "parse_uri" to "Parse URI",
    "verify_your_identity" to "Verify Your Identity",
    "scan_qr_code" to "Scan QR Code",
    "settings" to "Settings",
    "appearance" to "Appearance",
    "dynamic_color" to "Dynamic Color",
    "follow_system_desktop_for_theme_color" to "Follow System Desktop for Theme Color",
    "select_color" to "Select Color",
    "manually_select_a_color_as_seed" to "Manually Select a Color as Seed",
    "data" to "Data",
    "security_authentication" to "Security Authentication",
    "perform_security_verification_on_startup" to "Perform Security Verification on Startup",
    "system_has_not_registered_any_authentication_method" to "System has not registered any authentication method",
    "backup_and_restore" to "Backup and Restore",
    "data_cloud_backup_to_reduce_risk_of_accidental_loss" to "Data Cloud Backup to Reduce Risk of Accidental Loss",
    "About" to "About",
    "open_source_license" to "Open Source License",
    "no_them_no_me" to "No Them, No Me",
    "project_homepage" to "Project Homepage",
    "View_Source_Code_and_find_job" to "View Source Code and Give Me a Job Chance",
    "link_access_failed" to "Link access failed",
    "color_picker" to "Color picker",
    "cancel" to "Cancel",
    "OK" to "OK",
    "DEL" to "DEL",
    "Account" to "Account",
    "cloud_connection_type" to "Cloud Connection Type",
    "current_storage_location" to "Current Storage Location",
    "current_only_support_webdav_sorry" to "Current only support WebDav,Sorry!",
    "Current_Usage" to "Current Usage",
    "Login_Account" to "Login Account",
    "You_may_need_a_reliable_network_connection" to "You may need a reliable network connection.",
    "Storage_location" to "Storage location",
    "Current_storage_path" to "Current storage path",
    "Notice_RSA" to "Your data will be encrypted using RSA before being stored in the cloud. However the corresponding public and private keys can be found in the source code of this application. Please be cautious and ensure proper backup of your data.",
    "Export_Backup" to "Export Backup",
    "Local" to "Local",

    "Currently_backed_up_in" to "Currently backed up in",
    "Import_backup_file" to "Import backup file",
    "select restore file" to "select restore file",
    "Notice_only_app_itself" to "Only supports exporting data from the application itself",
    "Cloud_backup_location" to "Cloud backup location",
    "Cloud_backup_account" to "Cloud backup account",
    //webdav
    "password can't be empty" to "password can't be empty",
    "website can't be empty" to "website can't be empty",
    "WebDav Config" to "WebDav Config",
    "Login" to "Login",
    "login successful" to "login successful",
    "login failed" to  "login failed",
    "Link" to "Link",
    "UserName" to "UserName",
    "Password" to "Password",


    "Change_account" to "Change account",
    "Logout" to "Logout",
    "Third_Party_Disclaimer" to "Third Party Disclaimer",
    "Import_from_URI" to "Import from URI",
    "URI_Link" to "URI Link",
    "Import" to "Import",
    "Notice_valid_data" to "The URI link you imported does not contain valid data.",
    "Add_New_Item" to "Add New Item",
    "Save" to "Save",
    "Name" to "Name",
    "Service_Provider" to "Service Provider",
    "Access_Key" to "Access Key",
    "Time_Interval" to "Time Interval",
    "Counter" to "Counter",
    "Digits" to "Digits",
    "Hash_Function" to "Hash Function",
    "Name_cannot_be_empty" to "Name cannot be empty",
    "Access_Key_cannot_be_empty" to "Access Key cannot be empty",
    "Notice_validBase32encoding" to "Access Key is not a valid Base32 encoding",
    "Select_Image" to "Select Image",
    "Switch_Language" to "Switch Language",
    "The_default_setting_is_usually_fine" to "The default setting is usually fine",
    "Backup_location_not_selected_yet" to "Backup location not selected yet",
    "Cloud" to "Cloud",
    "Backup_Data" to "Backup Data",
    "Notice_care_your_privacy_on_cloud" to "Uploading data to the cloud carries risks. Please manage your privacy.",
    "Restore_Data" to "Restore Data",
    "Backup_file_not_found" to "Backup file not found",
    "File_Size" to "File Size",
    "Modification_Time" to "Modification Time",
    "Notice_WebDAV_first" to "Please log in to WebDAV first",
    "Long_press_to_switch_output_directory" to "Long press to switch output directory",
    "sorry_dynamic_color" to "Only Android 12 support dynamic color,Sorry!",
    "title_activity_language_dialog" to "LanguageDialog",
    "ERROR" to "Error",
    "TheQRcodeisinvalid" to "The valid QR code in the picture cannot be recognized, please try again with another one",
    "FailToAuthorizeWebDav" to "Fail to authorize WebDav application",
    "Prompt" to "Prompt",
    "dialog_setting" to "Go to Settings",
    "tip_perm_request_storage" to "needs storage access to find and read books. please go App Settingsto allow Storage permission.",
    "backup_success" to "Backup succeed",
    "backup_fail" to "Backup failed",
    "backup" to "backup",
    "Restore_the_backup" to "Restore the backup",
    "restoreFormWebDav" to "Restore form webdav",
    "Sync" to "Sync",
    "SyncFromLastBackup" to "Sync form last backup",
    "Copied" to "Copied!",
    // code Ui notice
    "please input website account" to "please input website account",
    "account can't be null" to "account can't be null",
    "please input vendor" to "please input vendor",
    "vendor can't be null" to "vendor can't be null",
    "please input access key" to "please input access key",
    "access key can't be null" to "access key can't be null",
    "please input time interval" to "please input time interval",
    "time interval can't be null" to "time interval can't be null",
    "Access Key is not a valid Base32 encoding" to "Access Key is not a valid Base32 encoding",
)


val languageCN: Map<String, String> = mapOf(
    "app_name" to "双步验证",
    "title_activity_main_view" to "主视图活动",
    "title_activity_main2" to "主要活动2",
    "theme_warn" to "注意：主题切换仅在浅色模式下生效，深色模式为纯黑色（为了省电）；但有一种特殊情况：即如果开启使用系统壁纸相关颜色，则主题颜色将被设置为与系统壁纸颜色相关。",
    "manual_input" to "手动输入",
    "Scan_QR_Code" to "扫描二维码",
    "parse_uri" to "解析 URI",
    "verify_your_identity" to "验证您的身份",
    "scan_qr_code" to "扫描二维码",
    "settings" to "设置",
    "appearance" to "外观",
    "dynamic_color" to "动态色彩",
    "follow_system_desktop_for_theme_color" to "主题颜色来自系统桌面",
    "select_color" to "选择颜色",
    "manually_select_a_color_as_seed" to "手动选择颜色作为种子",
    "data" to "数据",
    "security_authentication" to "安全认证",
    "perform_security_verification_on_startup" to "启动时执行安全验证",
    "system_has_not_registered_any_authentication_method" to "系统未注册任何认证方式",
    "backup_and_restore" to "备份还原",
    "data_cloud_backup_to_reduce_risk_of_accidental_loss" to "数据云备份，降低意外丢失风险",
    "About" to "关于",
    "open_source_license" to "开源许可证",
    "no_them_no_me" to "没有他们，就没有我",
    "project_homepage" to "项目主页",
    "View_Source_Code_and_find_job" to "看看源码，给个工作机会",
    "link_access_failed" to "链接访问失败",
    "color_picker" to "选色器",
    "cancel" to "取消",
    "OK" to "确定",
    "DEL" to "删除",
    "Account" to "帐户",
    "cloud_connection_type" to "云连接类型",
    "current_storage_location" to "当前存储于",
    "current_only_support_webdav_sorry" to "目前仅支持WebDav,Sorry!",
    "Current_Usage" to "当前使用情况",
    "Login_Account" to "登录账户",
    "You_may_need_a_reliable_network_connection" to "可能需要您有可靠的网络连接。",
    "Storage_location" to "存储位置",
    "Current_storage_path" to "当前存储路径",
    "Notice_RSA" to "您的数据在存储到云端之前将使用 RSA 进行加密。不过，可以在该应用程序的源代码中找到相应的公钥和私钥。请谨慎行事并确保正确备份您的数据。",
    "Export_Backup" to "导出备份",
    "Local" to "本地",

    "Currently_backed_up_in" to "目前备份于",
    "Import_backup_file" to "导入备份文件",
    "select restore file" to "选择恢复文件",
    "Notice_only_app_itself" to "仅支持从应用程序本身导出数据",
    "Cloud_backup_location" to "云备份位置",
    "Cloud_backup_account" to "云备份账号",

    //webdav
    "password can't be empty" to "密码不能为空",
    "website can't be empty" to "网络地址不能为空",
    "Login" to "登录",
    "WebDav Config" to "配置WebDav",
    "login successful" to "登录成功",
    "login failed" to  "登录失败",
    "Link" to "链接地址",
    "UserName" to "用户名",
    "Password" to "密码",

    "Change_account" to "更改帐户",
    "Logout" to "登出",
    "Third_Party_Disclaimer" to "第三方声明",
    "Import_from_URI" to "从 URI 导入",
    "URI_Link" to "网址链接",
    "Import" to "导入",
    "Notice_valid_data" to "您导入的 URI 链接不包含有效数据。",
    "Add_New_Item" to "添加新项目",
    "Save" to "保存",
    "Name" to "名称",
    "Service_Provider" to "服务提供者",
    "Access_Key" to "访问密钥",
    "Time_Interval" to "时间间隔",
    "Counter" to "计数器",
    "Digits" to "位数",
    "Hash_Function" to "哈希函数",
    "Name_cannot_be_empty" to "名称不能为空",
    "Access_Key_cannot_be_empty" to "访问密钥不能为空",
    "Notice_validBase32encoding" to "Access Key不是有效的Base32编码",
    "Select_Image" to "选择图像",
    "Switch_Language" to "切换语言",
    "The_default_setting_is_usually_fine" to "默认设置通常很好",
    "Backup_location_not_selected_yet" to "尚未选择备份位置",
    "Cloud" to "云",
    "Backup_Data" to "备份数据",
    "Notice_care_your_privacy_on_cloud" to "将数据上传到云端会带来风险。请注意管理您的隐私",
    "Restore_Data" to "恢复数据",
    "Backup_file_not_found" to "找不到备份文件",
    "File_Size" to "文件大小",
    "Modification_Time" to "修改时间",
    "Notice_WebDAV_first" to "请先登录WebDAV",
    "Long_press_to_switch_output_directory" to "长按切换输出目录",
    "sorry_dynamic_color" to "只有安卓12支持动态颜色，对不起！",
    "title_activity_language_dialog" to "语言对话框",
    "ERROR" to "出现错误",
    "TheQRcodeisinvalid" to "无法识别图片中的有效二维码，请换一张重试",
    "PermissionNeed" to "该功能需要此权限，请打开该权限",
    "PermissionPlease" to "请求权限",
    "FailToAuthorizeWebDav" to "登录WebDav失败",
    "Prompt" to "提示",
    "dialog_setting" to "Go to Settings",
    "tip_perm_request_storage" to "需要访问存储卡权限，请前往“设置”—“应用权限”，打开所需权限",
    "backup_success" to "备份成功",
    "backup_fail" to "备份失败",
    "backup" to "备份",
    "Restore_the_backup" to "恢复备份",
    "restoreFormWebDav" to "从WebDav恢复备份",
    "Sync" to "同步",
    "SyncFromLastBackup" to "同步最后一次备份",
    "Copied" to "已复制!",

    // code Ui notice
    "please input website account" to "请输入站点账号",
    "account can't be null" to "账号不能为空",
    "please input vendor" to "请输入站点名称",
    "vendor can't be null" to "站点名称不能为空",
    "please input access key" to "请输入访问键",
    "access key can't be null" to "访问键不能为空",
    "please input time interval" to "请输入 time interval",
    "time interval can't be null" to "time interval 不能为空",
    "Access Key is not a valid Base32 encoding" to "访问键不是有效的Base32加密",

)
val locales: Map<String, Map<String, String>> = mapOf(
    "English" to languageEN,
    "简体中文" to languageCN
)

fun locale(string: String): String {
    return if (localeState.value.containsKey(string))
        localeState.value.getValue(string)
    else
        string
}