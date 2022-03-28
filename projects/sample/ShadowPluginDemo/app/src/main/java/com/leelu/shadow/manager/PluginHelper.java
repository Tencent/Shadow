package com.leelu.shadow.manager;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.leelu.constants.Constant;
import com.leelu.shadow.MyApplication;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * CreateDate: 2022/3/15 17:05
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
public class PluginHelper {
    public final String TAG = this.getClass().getSimpleName();
    public final static String sPluginManagerName = "plugin-manager-debug.apk";//动态加载的插件管理apk

    /**
     * 动态加载的插件包，里面包含以下几个部分，插件apk，插件框架apk（loader apk和runtime apk）, apk信息配置关系json文件
     */
    public final static String sPluginZip = "plugin-debug.zip";
//    public final static String sPluginZip2 = "plugin-debug-2.0.zip";

//    public final static String sLoaderAndRuntime = "loader-debug.zip";
    public File pluginManagerFile;
    public File pluginZipFile;
    public File pluginZipFile2;
    public File loaderAndRuntime;
    public ExecutorService singlePool = Executors.newSingleThreadExecutor();
    private Context mContext;

    private static PluginHelper sInstance = new PluginHelper();

    public static PluginHelper getInstance() {
        return sInstance;
    }

    private PluginHelper() {
    }

    @WorkerThread
    public void init(Context context) {
        Log.d(TAG,"init");
        pluginManagerFile = new File(context.getFilesDir(), sPluginManagerName);
        pluginZipFile = new File(context.getFilesDir(), sPluginZip);
//        pluginZipFile2 = new File(context.getFilesDir(), sPluginZip2);
//        loaderAndRuntime = new File(context.getFilesDir(), sLoaderAndRuntime);
        mContext = context.getApplicationContext();
        singlePool.execute(() -> {
            preparePlugin();
            MyApplication.getApp().loadPluginManager(pluginManagerFile);
            Log.d("PluginHelper", "initPluginHelper finish");
        });
    }

    private void preparePlugin() {
        try {
            InputStream is = mContext.getAssets().open(sPluginManagerName);
            FileUtils.copyInputStreamToFile(is, pluginManagerFile);

            InputStream zip = mContext.getAssets().open(sPluginZip);
            FileUtils.copyInputStreamToFile(zip, pluginZipFile);

//            InputStream zip2 = mContext.getAssets().open(sPluginZip2);
//            FileUtils.copyInputStreamToFile(zip2, pluginZipFile2);

//            InputStream zip3 = mContext.getAssets().open(sLoaderAndRuntime);
//            FileUtils.copyInputStreamToFile(zip3, loaderAndRuntime);
        } catch (IOException e) {
            throw new RuntimeException("从assets中复制apk出错", e);
        }
    }
}
