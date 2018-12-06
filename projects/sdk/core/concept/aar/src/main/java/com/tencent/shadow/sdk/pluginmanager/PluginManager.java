package com.tencent.shadow.sdk.pluginmanager;

import android.content.Context;
import android.os.Bundle;

/**
 * 提供给接入
 */
public interface PluginManager extends ILifecycle {

    /**
     * @param context context
     * @param formId  标识本次请求的来源位置，用于区分入口
     * @param bundle  参数列表
     */
    void enter(Context context, long formId, Bundle bundle);

}
