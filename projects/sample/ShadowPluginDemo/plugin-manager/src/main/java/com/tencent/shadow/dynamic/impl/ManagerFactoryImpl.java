package com.tencent.shadow.dynamic.impl;

import android.content.Context;

import com.leelu.plugin_manager.SamplePluginManager;
import com.tencent.shadow.dynamic.host.ManagerFactory;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;
/**
 * CreateDate: 2022/3/17 17:56
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */


//此类包名及类名固定
public final class ManagerFactoryImpl implements ManagerFactory {
    @Override
    public PluginManagerImpl buildManager(Context context) {
        return new SamplePluginManager(context);
    }
}
