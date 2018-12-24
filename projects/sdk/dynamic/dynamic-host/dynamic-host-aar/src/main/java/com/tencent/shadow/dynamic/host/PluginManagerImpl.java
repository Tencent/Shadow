package com.tencent.shadow.dynamic.host;

import android.os.Bundle;

/**
 * 实现方需要实现的接口
 *
 * @author cubershi
 */
public interface PluginManagerImpl extends PluginManager {

    void onCreate(Bundle bundle);

    void onSaveInstanceState(Bundle outState);

    void onDestroy();
}
