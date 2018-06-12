package com.tencent.cubershi.mock_interface;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Pair;
import android.view.ContextThemeWrapper;

public class MockContext extends ContextThemeWrapper {
    PluginActivityLauncher mPluginActivityLauncher;
    PluginServiceOperator mPluginServiceOperator;
    ClassLoader mPluginClassLoader;

    public final void setPluginClassLoader(ClassLoader classLoader) {
        mPluginClassLoader = classLoader;
    }

    public final void setServiceOperator(PluginServiceOperator pluginServiceOperator) {
        mPluginServiceOperator = pluginServiceOperator;
    }


    public void setPluginActivityLauncher(PluginActivityLauncher pluginActivityLauncher) {
        mPluginActivityLauncher = pluginActivityLauncher;
    }

    public interface PluginActivityLauncher {
        /**
         * 启动Actvity
         *
         * @param context 启动context
         * @param intent  插件内传来的Intent.
         * @return <code>true</code>表示该Intent是为了启动插件内Activity的,已经被正确消费了.
         * <code>false</code>表示该Intent不是插件内的Activity.
         */
        boolean startActivity(Context context, Intent intent);

    }

    public interface PluginServiceOperator {

        Pair<Boolean, ComponentName> startService(MockContext context, Intent intent);

        Pair<Boolean, Boolean> stopService(MockContext context, Intent name);

        Pair<Boolean, Boolean> bindService(MockContext context, Intent service, ServiceConnection conn, int flags);

        Pair<Boolean, ?> unbindService(MockContext context, ServiceConnection conn);

    }

    @Override
    public void startActivity(Intent intent) {
        final Intent pluginIntent = new Intent(intent);
        pluginIntent.setExtrasClassLoader(mPluginClassLoader);
        final boolean success = mPluginActivityLauncher.startActivity(this, pluginIntent);
        if (!success) {
            super.startActivity(intent);
        }
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        if (!mPluginServiceOperator.unbindService(this, conn).first)
            super.unbindService(conn);
        return;
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        Pair<Boolean, Boolean> ret = mPluginServiceOperator.bindService(this, service, conn, flags);
        if (!ret.first)
            return super.bindService(service, conn, flags);
        return ret.second;
    }

    @Override
    public boolean stopService(Intent name) {
        Pair<Boolean, Boolean> ret = mPluginServiceOperator.stopService(this, name);
        if (!ret.first)
            return super.stopService(name);
        return ret.second;
    }

    @Override
    public ComponentName startService(Intent service) {
        Pair<Boolean, ComponentName> ret = mPluginServiceOperator.startService(this, service);
        if (!ret.first)
            return super.startService(service);
        return ret.second;
    }
}
