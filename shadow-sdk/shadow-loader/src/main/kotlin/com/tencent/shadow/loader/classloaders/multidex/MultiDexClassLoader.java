package com.tencent.shadow.loader.classloaders.multidex;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

import dalvik.system.DexClassLoader;

/**
 * Created by cubershi on 2016/12/20.
 */

public class MultiDexClassLoader extends DexClassLoader {

    public MultiDexClassLoader(Context hostContext, String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
        SharedPreferences pluginLoaderMultiDex = hostContext.getSharedPreferences("PluginLoaderMultiDex", Context.MODE_PRIVATE);
        MultiDex.install(this, dexPath, new File(optimizedDirectory), pluginLoaderMultiDex);
    }
}
