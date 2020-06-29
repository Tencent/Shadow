package com.tencent.shadow.dynamic.manager;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.load_parameters.LoadParameters;
import com.tencent.shadow.core.manager.BasePluginManager;
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.manager.installplugin.InstalledType;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.host.NotFoundException;
import com.tencent.shadow.dynamic.host.PluginManagerImpl;

abstract public class BaseDynamicPluginManager extends BasePluginManager implements UuidManagerImpl, PluginManagerImpl {
    private static final Logger mLogger = LoggerFactory.getLogger(BaseDynamicPluginManager.class);

    protected ProcessLoader processLoader;

    public BaseDynamicPluginManager(Context context) {
        super(context);
        processLoader = new ProcessLoader(mHostContext, this);
    }

    /**
     * PluginManager对象创建的时候回调
     *
     * @param bundle 当PluginManager有更新时会回调老的PluginManager对象onSaveInstanceState存储数据，bundle不为null说明发生了更新
     *               为null说明是首次创建
     */
    public void onCreate(Bundle bundle) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onCreate bundle:" + bundle);
        }
    }

    /**
     * 当PluginManager有更新时会先回调老的PluginManager对象 onSaveInstanceState存储数据
     *
     * @param bundle 要存储的数据
     */
    public void onSaveInstanceState(Bundle bundle) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onSaveInstanceState:" + bundle);
        }
    }

    /**
     * 当PluginManager有更新时先会销毁老的PluginManager对象，回调对应的onDestroy
     */
    public void onDestroy() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onDestroy:");
        }
    }

    public InstalledApk getPlugin(String uuid, String partKey) throws FailedException, NotFoundException {
        try {
            InstalledPlugin.Part part;
            try {
                part = getPluginPartByPartKey(uuid, partKey);
            } catch (RuntimeException e) {
                throw new NotFoundException("uuid==" + uuid + "partKey==" + partKey + "的Plugin找不到");
            }
            String businessName = part instanceof InstalledPlugin.PluginPart ? ((InstalledPlugin.PluginPart) part).businessName : null;
            String[] dependsOn = part instanceof InstalledPlugin.PluginPart ? ((InstalledPlugin.PluginPart) part).dependsOn : null;
            String[] hostWhiteList = part instanceof InstalledPlugin.PluginPart ? ((InstalledPlugin.PluginPart) part).hostWhiteList : null;
            LoadParameters loadParameters
                    = new LoadParameters(businessName, partKey, dependsOn, hostWhiteList);

            Parcel parcelExtras = Parcel.obtain();
            loadParameters.writeToParcel(parcelExtras, 0);
            byte[] parcelBytes = parcelExtras.marshall();
            parcelExtras.recycle();

            return new InstalledApk(
                    part.pluginFile.getAbsolutePath(),
                    part.oDexDir == null ? null : part.oDexDir.getAbsolutePath(),
                    part.libraryDir == null ? null : part.libraryDir.getAbsolutePath(),
                    parcelBytes
            );
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("getPlugin exception:", e);
            }
            throw new FailedException(e);
        }
    }

    private InstalledApk getInstalledPL(String uuid, int type) throws FailedException, NotFoundException {
        try {
            InstalledPlugin.Part part;
            try {
                part = getLoaderOrRunTimePart(uuid, type);
            } catch (RuntimeException e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("getInstalledPL exception:", e);
                }
                throw new NotFoundException("uuid==" + uuid + " type==" + type + "没找到。cause：" + e.getMessage());
            }
            return new InstalledApk(part.pluginFile.getAbsolutePath(),
                    part.oDexDir == null ? null : part.oDexDir.getAbsolutePath(),
                    part.libraryDir == null ? null : part.libraryDir.getAbsolutePath());
        } catch (RuntimeException e) {
            throw new FailedException(e);
        }
    }

    public InstalledApk getPluginLoader(String uuid) throws FailedException, NotFoundException {
        return getInstalledPL(uuid, InstalledType.TYPE_PLUGIN_LOADER);
    }

    public InstalledApk getRuntime(String uuid) throws FailedException, NotFoundException {
        return getInstalledPL(uuid, InstalledType.TYPE_PLUGIN_RUNTIME);
    }
}
