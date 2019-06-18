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

package com.tencent.shadow.test.none_dynamic.host;

import android.content.Context;

import com.tencent.shadow.core.common.InstalledApk;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PreparePluginApkBloc {
    private static final Logger mLogger = LoggerFactory.getLogger(PreparePluginApkBloc.class);
    private final String mPluginFileName;
    private boolean mPrepared = false;
    private InstalledApk mPreparePlugin;

    public PreparePluginApkBloc(String mPluginFileName) {
        this.mPluginFileName = mPluginFileName;
    }

    InstalledApk preparePlugin(Context context) {
        if (mPrepared) {
            return mPreparePlugin;
        }
        mPrepared = true;
        if (mLogger.isInfoEnabled()) {
            mLogger.info("preparePlugin");
        }
        //宿主程序必须加载过armeabi的so,插件才可以以armeabi ABI兼容模式运行,否则在64位手机上,系统会加载插件的arm64 ABI的so。
//        System.loadLibrary("encry");//这是一个随意拿来的armeabi的so。

        copyFile(context, mPluginFileName);

        InstalledApk installedPlugin = installPlugin(getApkFileInDataDir(context, mPluginFileName));
        mPreparePlugin = installedPlugin;
        return installedPlugin;
    }

    private static void copyFile(Context context, String name) {
        File pluginFile = getApkFileInDataDir(context, name);
        if (pluginFile.exists()) {
            pluginFile.delete();
        }
        try {
            copyFromAssetsToData(context, name);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyFromAssetsToData(Context context, String filename) throws IOException {
        InputStream is = context.getAssets().open(filename);
        File destination = getApkFileInDataDir(context, filename);
        if (destination.exists()) {
            destination.delete();
        }
        FileUtils.copyInputStreamToFile(is, destination);
    }

    public static File getApkFileInDataDir(Context context, String filename) {
        return new File(context.getFilesDir() + "/" + filename);
    }

    private static InstalledApk installPlugin(File apk) {
        File[] classLoaderDirs = prepareClassLoaderDirs(apk);
        return new InstalledApk(
                apk.getAbsolutePath(),
                getOdexDir(classLoaderDirs).getAbsolutePath(),
                getLibDir(classLoaderDirs).getAbsolutePath()
        );
    }

    private static File getOdexDir(File[] classLoaderDirs) {
        return classLoaderDirs[0];
    }

    private static File getLibDir(File[] classLoaderDirs) {
        return classLoaderDirs[1];
    }

    private static File[] prepareClassLoaderDirs(File apk) {
        File odexDir = new File(apk.getParent(), apk.getName() + "_odex");
        File libDir = new File(apk.getParent(), apk.getName() + "_lib");
        prepareDirs(odexDir, libDir);
        return new File[]{odexDir, libDir};
    }

    private static void prepareDirs(File odexDir, File libDir) {
        if (odexDir.exists() && !odexDir.isDirectory()) {
            throw new RuntimeException("odexDir目标路径" + odexDir.getAbsolutePath()
                    + "已被其他文件占用");
        } else if (!odexDir.exists()) {
            boolean success = odexDir.mkdir();
            if (!success) {
                throw new RuntimeException("odexDir目标路径" + odexDir.getAbsolutePath()
                        + "创建目录失败");
            }
        }

        if (!libDir.exists()) {
            if (!libDir.mkdirs()) {
                throw new RuntimeException("libDir目标路径" + libDir.getAbsolutePath()
                        + "创建目录失败");
            }
        }
    }
}
