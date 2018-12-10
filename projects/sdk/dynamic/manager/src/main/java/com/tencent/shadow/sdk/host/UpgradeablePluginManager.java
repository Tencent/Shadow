package com.tencent.shadow.sdk.host;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.tencent.shadow.core.host.PluginManager;
import com.tencent.shadow.core.host.ViewCallback;
import com.tencent.shadow.core.host.common.annotation.API;
import com.tencent.shadow.core.host.common.classloader.ApkClassLoader;
import com.tencent.shadow.sdk.host.download.DownloadException;
import com.tencent.shadow.sdk.host.download.Downloader;
import com.tencent.shadow.sdk.host.download.LengthHashURLConnectionDownloader;
import com.tencent.shadow.sdk.host.download.TargetDownloadInfo;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.tencent.shadow.sdk.dynamic.BuildConfig.COMPATIBLE_SUFFIX;


public class UpgradeablePluginManager {

    private final String appType;

    private final Object mLock = new Object();

    private final static String TAG = "PluginManager";

    /**
     * 宿主提供的根目录
     */
    protected final File mPluginManagerRootDir;
    /**
     * 在mPluginManagerRootDir中创建的PluginManager存放文件的目录
     */
    protected final File mPluginStoreDir;
    /**
     * 实际工作的PluginManager
     */
    protected PluginManager mActualPluginManager;
    /**
     * 远程PluginManager实现的下载地址
     */
    private static final String REMOTE_PLUGIN_MANAGER_URL = "http://dldir1.qq.com/huayang/Android/ShadowPluginManager" + COMPATIBLE_SUFFIX;
    /**
     * 远程PluginManager下载回来保存的文件名
     */
    private static final String REMOTE_PLUGIN_MANAGER_APK_FILENAME = "ShadowPluginManager" + COMPATIBLE_SUFFIX + ".apk";
    /**
     * 远程PluginManager的odex目录名
     */
    private static final String REMOTE_PLUGIN_MANAGER_ODEX_DIRNAME = "ShadowPluginManager" + COMPATIBLE_SUFFIX + "_odex";
    /**
     * 远程PluginManager下载过程中临时文件文件名
     */
    private static final String REMOTE_PLUGIN_MANAGER_APK_TMP_FILENAME = "ShadowPluginManager" + COMPATIBLE_SUFFIX + ".apk.downloading";
    /**
     * 系统中可读的临时文件夹。这是个特殊的文件夹，可以无需root权限通过adb push进来，程序也可以直接读取。
     * 主要用于测试包直接在本地加载
     */
    private static final File TEMP_DIR = new File("/data/local/tmp");
    /**
     * 远程PluginManager的apk文件
     */
    private final File mRemotePluginManagerApk;

    private static final String DEFAULT_STORE_DIR_NAME = "ShadowPluginManager";
    /**
     * 远程PluginManager依赖的接口包名
     */
    protected static final String[] REMOTE_PLUGIN_MANAGER_INTERFACES = new String[]
            {
                    "com.tencent.shadow.core.host",
                    "com.tencent.shadow.core.host.common",
                    "com.tencent.commonsdk.zip",
                    "com.tencent.mobileqq.intervideo.now.dynamic",
                    "com.tencent.qqinterface"
            };
    /**
     * 远程PluginManager中实现PluginManager接口的类的类名
     */
    protected static final String REMOTE_PLUGIN_MANAGER_IMPL_CLASS_NAME = "com.tencent.shadow.sdk.pluginmanager.PluginManagerImpl";

    /**
     * 没有进行升级
     */
    private static final int UPGRADE_STATE_NO_UPDATE = -1;
    /**
     * 升级成功
     */
    private static final int UPGRADE_STATE_UPDATE_SUCCESS = 1;
    /**
     * 升级失败
     */
    private static final int UPGRADE_STATE_UPDATE_FAILED = 0;

    /**
     * PluginManager apk文件
     */
    private File mPluginManagerFile;

    /**
     * 判断测试环境的文件
     */
    private final File mDevFile;

    /**
     * 是否测试环境
     */
    private final boolean mDevMode;
    /**
     * PluginManager下载地址
     */
    private final String mDownloadUrl;
    /**
     * PluginManager文件最后的修改时间
     */
    private long mPMLastModified;


    public UpgradeablePluginManager(File rootFile, String appType) {
        this.appType = appType;
        mPluginManagerRootDir = rootFile;
        mPluginStoreDir = new File(mPluginManagerRootDir, DEFAULT_STORE_DIR_NAME + "_" + appType);
        mRemotePluginManagerApk = new File(mPluginStoreDir, REMOTE_PLUGIN_MANAGER_APK_FILENAME);
        mDevFile = new File(TEMP_DIR, DEFAULT_STORE_DIR_NAME + "_" + appType + ".apk");
        mDevMode = mDevFile.exists();
        mDownloadUrl = REMOTE_PLUGIN_MANAGER_URL + "_" + appType;
    }

    /**
     * TEST用途
     *
     * @param mPluginManagerRootDir
     * @param mPluginStoreDir
     * @param mRemotePluginManagerApk
     */
    public UpgradeablePluginManager(File mPluginManagerRootDir, File mPluginStoreDir, File mRemotePluginManagerApk) {
        this.mPluginManagerRootDir = mPluginManagerRootDir;
        this.mPluginStoreDir = mPluginStoreDir;
        this.mRemotePluginManagerApk = mRemotePluginManagerApk;
        appType = "local";
        mDevFile = null;
        mDevMode = false;
        mDownloadUrl = "local";

        mPluginManagerFile = mRemotePluginManagerApk;
    }

    /**
     * 【同步方法】如果有必要则升级PluginManager，然后初始化。
     *
     * @param timeout 超时时间。
     * @param unit    timeout的单位。
     * @return 初始化过程的状态  -1为没有进行升级  1升级成功  0升级失败
     */
    @API
    public int upgradeIfNeededThenInit(long timeout, TimeUnit unit) throws DownloadException {
        int state = UPGRADE_STATE_NO_UPDATE;
        if (mDevMode) {
            mPluginManagerFile = mDevFile;
            state = UPGRADE_STATE_UPDATE_SUCCESS;
        } else {
            synchronized (mLock) {
                long lastModified = mRemotePluginManagerApk.lastModified();
                if (lastModified == 0) {//首次进行下载
                    Future<File> fileFuture = downloadRemotePluginManager(
                            mDownloadUrl,
                            mRemotePluginManagerApk,
                            new File(mRemotePluginManagerApk.getParentFile(), REMOTE_PLUGIN_MANAGER_APK_TMP_FILENAME)
                    );
                    try {
                        mPluginManagerFile = fileFuture.get(timeout, unit);
                        state = UPGRADE_STATE_UPDATE_SUCCESS;
                    } catch (Exception e) {
                        throw new DownloadException("下载PluginManager失败", e);
                    }
                } else {
                    mPluginManagerFile = mRemotePluginManagerApk;
                }
            }
        }
        return state;
    }


    /**
     * 加载远程PluginManager的apk文件到ApkClassLoader中。
     *
     * @param file                  apk文件
     * @param odexDir               odex目录
     * @param interfacePackageNames 依赖的接口包名
     * @return 加载了远程PluginManager实现的ApkClassLoader
     * @throws IOException 清理odex目录时发生异常时抛出
     */
    protected ApkClassLoader loadRemotePluginManagerApk(File file, File odexDir, String[] interfacePackageNames) throws IOException {
        if (odexDir.exists() && (!odexDir.isDirectory())) {
            throw new IOException(odexDir.getAbsolutePath() + "已存在且不是目录");
        }
        odexDir.mkdirs();
        return new ApkClassLoader(file.getAbsolutePath(),
                odexDir.getAbsolutePath(),
                null,
                UpgradeablePluginManager.class.getClassLoader(),
                interfacePackageNames);
    }


    /**
     * 下载远程PluginManager
     *
     * @param url        下载地址
     * @param outputFile 存放路径
     * @param tmpFile    临时文件存放路径
     * @return Future
     */
    protected Future<File> downloadRemotePluginManager(String url, File outputFile, File tmpFile) {
        Downloader downloader = new LengthHashURLConnectionDownloader();
        TargetDownloadInfo targetDownloadInfo = new TargetDownloadInfo(url, "", 0);
        return downloader.download(targetDownloadInfo, outputFile, tmpFile);
    }

    public void enter(Context context, long fromId, Bundle bundle, ViewCallback viewCallback) throws Exception {
        boolean needCheckDownload = !mDevMode && mPMLastModified != 0;//非首次下载需要后台检测更新
        long lastModified = mPluginManagerFile.lastModified();
        if (lastModified != mPMLastModified || mActualPluginManager == null) {//有PM更新或者还没初始化PM时 创建新的PluginManager实例
            File dexFile = new File(mPluginStoreDir, REMOTE_PLUGIN_MANAGER_ODEX_DIRNAME + "_" + mPluginManagerFile.lastModified());
            ApkClassLoader apkClassLoader = loadRemotePluginManagerApk(mPluginManagerFile, dexFile, REMOTE_PLUGIN_MANAGER_INTERFACES);
            Bundle saveSate = null;
            if (mActualPluginManager != null) {
                saveSate = new Bundle();
                mActualPluginManager.onSaveInstanceState(saveSate);
                mActualPluginManager.onDestroy();
            }
            mActualPluginManager = apkClassLoader.getInterface(PluginManager.class, REMOTE_PLUGIN_MANAGER_IMPL_CLASS_NAME,
                    new Class[]{String.class, Context.class, ViewCallback.class, String.class},
                    new Object[]{appType, context, viewCallback, mPluginManagerFile.getAbsolutePath()});
            mActualPluginManager.onCreate(saveSate);
            Log.d(TAG, "lastModified:" + lastModified + " mPMLastModified:" + mPMLastModified);
        }
        mActualPluginManager.enter(context, fromId, bundle);
        mPMLastModified = lastModified;
        if (needCheckDownload) {
            downloadRemotePluginManager(
                    mDownloadUrl,
                    mRemotePluginManagerApk,
                    new File(mRemotePluginManagerApk.getParentFile(), REMOTE_PLUGIN_MANAGER_APK_TMP_FILENAME));
        }
    }


}
