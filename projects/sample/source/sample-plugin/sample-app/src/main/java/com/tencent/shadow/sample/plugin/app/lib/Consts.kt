package com.tencent.shadow.sample.plugin.app.lib

import android.os.Environment

object Consts {
    /**
     * app_id是从微信官网申请到的合法APPid
     */
    const val APP_ID_WX = "wx93d58c2a8fb8ce9a"

    /**
     * 微信AppSecret值
     */
    const val APP_SECRET_WX = "5fb7b06eb59a8fef0ed7c182e888a55b"

    const val QQ_LOGIN_APP_ID = "101852855"

    var TEMPIMAGEPATH = "${Environment.getExternalStorageDirectory()}/xrcloud/tempimg/"
}