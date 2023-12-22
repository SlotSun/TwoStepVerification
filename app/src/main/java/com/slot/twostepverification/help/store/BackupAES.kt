package com.slot.twostepverification.help.store

import cn.hutool.crypto.symmetric.AES
import com.slot.twostepverification.const.LocalConfig
import com.slot.twostepverification.utils.encoding.MD5Utils

class BackupAES : AES(
    MD5Utils.md5Encode(LocalConfig.password).encodeToByteArray(0, 16)
)