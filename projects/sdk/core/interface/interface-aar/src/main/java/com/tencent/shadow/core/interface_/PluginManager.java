package com.tencent.shadow.core.interface_;

import android.content.Context;
import android.os.Bundle;

/**
 * 提供给接入
 */
public interface PluginManager {

    /**
     * @param context context
     * @param formId  标识本次请求的来源位置，用于区分入口
     * @param bundle  参数列表
     * @param callback 用于从PluginManager实现中返回View
     */
    void enter(Context context, long formId, Bundle bundle, EnterCallback callback);

    void onCreate(Bundle bundle);

    void onSaveInstanceState(Bundle outState);

    void onDestroy();
}
