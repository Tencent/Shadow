package com.tencent.shadow.dynamic.host;

import android.app.Application;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.shadow.core.common.InstalledApk;

import java.io.File;
import java.util.HashMap;

import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_FILE_NOT_FOUND_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RELOAD_LOADER_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RELOAD_RUNTIME_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RESET_UUID_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_RUNTIME_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION;
import static com.tencent.shadow.dynamic.host.FailedException.ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION;

public class MultiLoaderPluginProcessService extends BasePluginProcessService {

    static final ActivityHolder sActivityHolder = new ActivityHolder();
    private final MultiLoaderPpsBinder mPpsControllerBinder = new MultiLoaderPpsBinder(this);

    private HashMap<String, String> mUuidMap = new HashMap<>();
    private HashMap<String, UuidManager> mUuidManagerMap = new HashMap<>();
    private HashMap<String, PluginLoaderImpl> mPluginLoaderMap = new HashMap<>();
    private HashMap<String, Boolean> mRuntimeLoadedMap = new HashMap<>();

    public static Application.ActivityLifecycleCallbacks getActivityHolder() {
        return sActivityHolder;
    }

    public static MultiLoaderPpsController wrapBinder(IBinder ppsBinder) {
        return new MultiLoaderPpsController(ppsBinder);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("onBind:" + this);
        }
        return mPpsControllerBinder;
    }

    synchronized void loadRuntimeForPlugin(String pluginKey, String uuid) throws FailedException {
        String logIdentity = "pluginKey=" + pluginKey + "|uuid=" + uuid;
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadRuntimeForPlugin:" + logIdentity);
        }

        UuidManager uuidManager = checkUuidManagerNotNull(pluginKey);
        addUuidForPlugin(pluginKey, uuid);
        if (isRuntimeLoaded(pluginKey)) {
            throw new FailedException(ERROR_CODE_RELOAD_RUNTIME_EXCEPTION, "重复调用loadRuntime," + logIdentity);
        }
        try {

            InstalledApk installedApk;
            try {
                installedApk = uuidManager.getRuntime(uuid);
            } catch (RemoteException e) {
                Log.i("PluginProcessService", "uuidManager.getRuntime new FailedException");
                throw new FailedException(ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION, e.getMessage());
            } catch (NotFoundException e) {
                Log.i("PluginProcessService", "uuidManager.getRuntime new NotFoundException");
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, "pluginKey=" + pluginKey + ", uuid=" + uuid + "的Runtime没有找到。cause:" + e.getMessage());
            }

            InstalledApk installedRuntimeApk = new InstalledApk(installedApk.apkFilePath, installedApk.oDexPath, installedApk.libraryPath);
            MultiDynamicContainer.loadContainerApk(pluginKey, installedRuntimeApk);
            markRuntimeLoaded(pluginKey);
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadRuntimeForPlugin发生RuntimeException", e);
            }
            throw new FailedException(e);
        }
    }

    synchronized void loadPluginLoaderForPlugin(String pluginKey, String uuid) throws FailedException {
        String logIdentity = "pluginKey=" + pluginKey + "|uuid=" + uuid;
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPluginLoader:" + logIdentity);
        }
        UuidManager uuidManager = checkUuidManagerNotNull(pluginKey);
        addUuidForPlugin(pluginKey, uuid);
        if (mPluginLoaderMap.get(pluginKey) != null) {
            throw new FailedException(ERROR_CODE_RELOAD_LOADER_EXCEPTION, "重复调用loadPluginLoader");
        }
        try {
            InstalledApk installedApk;
            try {
                installedApk = uuidManager.getPluginLoader(uuid);
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("取出" + logIdentity + "的Loader apk:" + installedApk.apkFilePath);
                }
            } catch (RemoteException e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("获取Loader Apk失败", e);
                }
                throw new FailedException(ERROR_CODE_UUID_MANAGER_DEAD_EXCEPTION, e.getMessage());
            } catch (NotFoundException e) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, logIdentity + "的PluginLoader没有找到。cause:" + e.getMessage());
            }
            File file = new File(installedApk.apkFilePath);
            if (!file.exists()) {
                throw new FailedException(ERROR_CODE_FILE_NOT_FOUND_EXCEPTION, file.getAbsolutePath() + "文件不存在");
            }

            PluginLoaderImpl pluginLoader = new LoaderImplLoader().load(installedApk, uuid, getApplicationContext());
            pluginLoader.setUuidManager(uuidManager);
            mPluginLoaderMap.put(pluginKey, pluginLoader);
        } catch (RuntimeException e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadPluginLoader发生RuntimeException", e);
            }
            throw new FailedException(e);
        } catch (FailedException e) {
            throw e;
        } catch (Exception e) {
            if (mLogger.isErrorEnabled()) {
                mLogger.error("loadPluginLoader发生Exception", e);
            }
            String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            throw new FailedException(ERROR_CODE_RUNTIME_EXCEPTION, "加载动态实现失败 cause：" + msg);
        }
    }

    synchronized void setUuidManagerForPlugin(String pluginKey, UuidManager uuidManager) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("setUuidManagerForPlugin pluginKey=" + pluginKey + ", uuidManager==" + uuidManager);
        }
        mUuidManagerMap.put(pluginKey, uuidManager);
        PluginLoaderImpl pluginLoader = mPluginLoaderMap.get(pluginKey);
        if (pluginLoader != null) {
            if (mLogger.isInfoEnabled()) {
                mLogger.info("更新PluginLoader的uuidManager");
            }
            pluginLoader.setUuidManager(uuidManager);
        }
    }

    synchronized PpsStatus getPpsStatusForPlugin(String pluginKey) {
        return new PpsStatus(mUuidMap.get(pluginKey), isRuntimeLoaded(pluginKey), mPluginLoaderMap.get(pluginKey) != null, mUuidManagerMap.get(pluginKey) != null);
    }

    synchronized IBinder getPluginLoaderForPlugin(String pluginKey) {
        return mPluginLoaderMap.get(pluginKey);
    }

    void exit() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("exit ");
        }
        MultiLoaderPluginProcessService.sActivityHolder.finishAll();
        System.exit(0);
        try {
            wait();
        } catch (InterruptedException ignored) {
        }
    }

    private UuidManager checkUuidManagerNotNull(String pluginKey) throws FailedException {
        UuidManager uuidManager = mUuidManagerMap.get(pluginKey);
        if (uuidManager == null) {
            throw new FailedException(ERROR_CODE_UUID_MANAGER_NULL_EXCEPTION, "mUuidManager == null");
        }
        return uuidManager;
    }

    private boolean isRuntimeLoaded(String pluginKey) {
        Boolean result = mRuntimeLoadedMap.get(pluginKey);
        return result != null && result;
    }

    private void markRuntimeLoaded(String pluginKey) {
        mRuntimeLoadedMap.put(pluginKey, true);
    }

    private void addUuidForPlugin(String pluginKey, String uuid) throws FailedException {
        String preUuid = mUuidMap.get(pluginKey);
        if (preUuid != null && !TextUtils.equals(uuid, preUuid)) {
            throw new FailedException(ERROR_CODE_RESET_UUID_EXCEPTION, "Plugin=" + pluginKey + "已设置过uuid==" + preUuid + ", 试图设置uuid==" + uuid);
        } else if (preUuid == null) {
            mUuidMap.put(pluginKey, uuid);
        }
    }
}
