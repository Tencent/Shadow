/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.runtime;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;

import com.tencent.shadow.core.runtime.container.GeneratedHostActivityDelegator;
import com.tencent.shadow.core.runtime.container.HostActivityDelegator;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class ShadowContext extends SubDirContextThemeWrapper {
    PluginComponentLauncher mPluginComponentLauncher;
    ClassLoader mPluginClassLoader;
    ShadowApplication mShadowApplication;
    Resources mPluginResources;
    Resources mMixResources;
    LayoutInflater mLayoutInflater;
    ApplicationInfo mApplicationInfo;
    protected String mPartKey;
    private String mBusinessName;
    /**
     * BroadcastReceiver到BroadcastReceiverWrapper对象到映射关系
     * <p>
     * 采用WeakHashMap<BroadcastReceiver, WeakReference<BroadcastReceiverWrapper>>
     * 使key和value都采用弱引用持有，以保持原本BroadcastReceiver的GC回收时机。
     * <p>
     * BroadcastReceiver由原有业务代码强持有（也可能不持有），BroadcastReceiver原本在registerReceiver
     * 之后交由系统持有，现在由BroadcastReceiverWrapper代替它被系统强持有。
     * 所以BroadcastReceiverWrapper强引用持有BroadcastReceiver，保持了系统强引用BroadcastReceiver的关系。
     * <p>
     * 如果业务原本没有持有BroadcastReceiver，也就不会再有unregisterReceiver调用来，
     * 也就不需要Map中有wrapper对应关系，所以用弱引用持有此关系没有影响。
     */
    final private Map<BroadcastReceiver, WeakReference<BroadcastReceiverWrapper>>
            mReceiverWrapperMap = new WeakHashMap<>();

    public ShadowContext() {
    }

    public ShadowContext(Context base, int themeResId) {
        super(base, themeResId);
    }

    public final void setPluginResources(Resources resources) {
        mPluginResources = resources;
    }

    public final void setPluginClassLoader(ClassLoader classLoader) {
        mPluginClassLoader = classLoader;
    }

    public void setPluginComponentLauncher(PluginComponentLauncher pluginComponentLauncher) {
        mPluginComponentLauncher = pluginComponentLauncher;
    }

    public void setShadowApplication(ShadowApplication shadowApplication) {
        mShadowApplication = shadowApplication;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        ApplicationInfo copy = new ApplicationInfo(applicationInfo);
        copy.metaData = null;//正常通过Context获得的ApplicationInfo就没有metaData
        mApplicationInfo = copy;
    }

    public void setBusinessName(String businessName) {
        if (TextUtils.isEmpty(businessName)) {
            businessName = null;
        }
        this.mBusinessName = businessName;
    }

    public void setPluginPartKey(String partKey) {
        this.mPartKey = partKey;
    }

    @Override
    public Context getApplicationContext() {
        return mShadowApplication;
    }

    @Override
    public Resources getResources() {
        if (mMixResources == null) {
            Context baseContext = getBaseContext();
            Resources hostResources;
            if (baseContext instanceof HostActivityDelegator) {
                hostResources = ((HostActivityDelegator) baseContext).superGetResources();
            } else {
                hostResources = baseContext.getResources();
            }
            mMixResources = new MixResources(hostResources, mPluginResources);
        }
        return mMixResources;
    }

    @Override
    public AssetManager getAssets() {
        return mPluginResources.getAssets();
    }

    @Override
    public Object getSystemService(String name) {
        if (LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (mLayoutInflater == null) {
                LayoutInflater inflater = (LayoutInflater) super.getSystemService(name);
                mLayoutInflater = ShadowLayoutInflater.build(inflater, this, mPartKey);
            }
            return mLayoutInflater;
        }
        return super.getSystemService(name);
    }

    @Override
    public ClassLoader getClassLoader() {
        return mPluginClassLoader;
    }

    public interface PluginComponentLauncher {
        /**
         * 启动Activity
         *
         * @param shadowContext 启动context
         * @param intent        插件内传来的Intent.
         * @return <code>true</code>表示该Intent是为了启动插件内Activity的,已经被正确消费了.
         * <code>false</code>表示该Intent不是插件内的Activity.
         */
        boolean startActivity(ShadowContext shadowContext, Intent intent, Bundle options);

        /**
         * 启动Activity
         *
         * @param delegator       发起启动的activity的delegator
         * @param intent          插件内传来的Intent.
         * @param callingActivity 调用者
         * @return <code>true</code>表示该Intent是为了启动插件内Activity的,已经被正确消费了.
         * <code>false</code>表示该Intent不是插件内的Activity.
         */
        boolean startActivityForResult(GeneratedHostActivityDelegator delegator, Intent intent, int requestCode, Bundle option, ComponentName callingActivity);

        Pair<Boolean, ComponentName> startService(ShadowContext context, Intent service);

        Pair<Boolean, Boolean> stopService(ShadowContext context, Intent name);

        Pair<Boolean, Boolean> bindService(ShadowContext context, Intent service, ServiceConnection conn, int flags);

        Pair<Boolean, ?> unbindService(ShadowContext context, ServiceConnection conn);

        Intent convertPluginActivityIntent(Intent pluginIntent);

    }

    @Override
    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        final Intent pluginIntent = new Intent(intent);
        pluginIntent.setExtrasClassLoader(mPluginClassLoader);
        final boolean success = mPluginComponentLauncher.startActivity(this, pluginIntent, options);
        if (!success) {
            super.startActivity(intent, options);
        }
    }

    @android.annotation.TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void superStartActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        if (!mPluginComponentLauncher.unbindService(this, conn).first)
            super.unbindService(conn);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        if (service.getComponent() == null) {
            return super.bindService(service, conn, flags);
        }
        Pair<Boolean, Boolean> ret = mPluginComponentLauncher.bindService(this, service, conn, flags);
        if (!ret.first)
            return super.bindService(service, conn, flags);
        return ret.second;
    }

    @Override
    public boolean stopService(Intent name) {
        if (name.getComponent() == null) {
            return super.stopService(name);
        }
        Pair<Boolean, Boolean> ret = mPluginComponentLauncher.stopService(this, name);
        if (!ret.first)
            return super.stopService(name);
        return ret.second;
    }

    @Override
    public ComponentName startService(Intent service) {
        if (service.getComponent() == null) {
            return super.startService(service);
        }
        Pair<Boolean, ComponentName> ret = mPluginComponentLauncher.startService(this, service);
        if (!ret.first)
            return super.startService(service);
        return ret.second;
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return mApplicationInfo;
    }

    public PluginComponentLauncher getPendingIntentConverter() {
        return mPluginComponentLauncher;
    }

    @Override
    String getSubDirName() {
        if (mBusinessName == null) {
            return null;
        } else {
            return "ShadowPlugin_" + mBusinessName;
        }
    }

    @Override
    public String getPackageName() {
        return mApplicationInfo.packageName;
    }


    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(receiverToWrapper(receiver), filter);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, int flags) {
        return super.registerReceiver(receiverToWrapper(receiver), filter, flags);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler) {
        return super.registerReceiver(receiverToWrapper(receiver), filter, broadcastPermission, scheduler);
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler, int flags) {
        return super.registerReceiver(receiverToWrapper(receiver), filter, broadcastPermission, scheduler, flags);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        synchronized (mReceiverWrapperMap) {
            WeakReference<BroadcastReceiverWrapper> weakReference
                    = mReceiverWrapperMap.get(receiver);
            BroadcastReceiverWrapper wrapper = weakReference == null ? null : weakReference.get();
            if (null != wrapper) {
                super.unregisterReceiver(wrapper);
            } else {
                super.unregisterReceiver(receiver);
            }
            mReceiverWrapperMap.remove(receiver);
        }
    }

    @Override
    public String getPackageCodePath() {
        PluginPartInfo pluginInfo = PluginPartInfoManager.getPluginInfo(getClassLoader());
        return pluginInfo.packageManager.getArchiveFilePath();
    }

    private BroadcastReceiverWrapper receiverToWrapper(BroadcastReceiver receiver) {
        if (receiver == null) {
            return null;
        }
        synchronized (mReceiverWrapperMap) {
            WeakReference<BroadcastReceiverWrapper> weakReference
                    = mReceiverWrapperMap.get(receiver);
            BroadcastReceiverWrapper wrapper = weakReference == null ? null : weakReference.get();
            if (wrapper == null) {
                wrapper = new BroadcastReceiverWrapper(receiver, this);
                mReceiverWrapperMap.put(receiver, new WeakReference<>(wrapper));
            }
            return wrapper;
        }
    }
}
