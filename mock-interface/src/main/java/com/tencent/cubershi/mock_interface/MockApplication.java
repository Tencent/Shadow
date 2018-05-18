package com.tencent.cubershi.mock_interface;

import android.content.Context;

/**
 * 用于在plugin-loader中调用假的Application方法的接口
 */
public interface MockApplication {
    void onCreate();

    Context getHostApplicationContext();

    void setHostApplicationContext(Context hostAppContext);
}
