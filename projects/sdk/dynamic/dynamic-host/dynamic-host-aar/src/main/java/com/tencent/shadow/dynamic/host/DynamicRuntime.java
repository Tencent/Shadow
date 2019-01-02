package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;

/**
 * 将runtime apk加载到DexPathClassLoader，形成如下结构的classLoader树结构
 * ---BootClassLoader
 * ----DexPathClassLoader
 * ------PathClassLoader
 */
public class DynamicRuntime {

    private static final Logger mLogger = LoggerFactory.getLogger(DynamicRuntime.class);

    private static final String SP_NAME = "ShadowRuntimeLoader";

    private static final String KEY_RUNTIME_APK = "KEY_RUNTIME_APK";
    private static final String KEY_RUNTIME_ODEX = "KEY_RUNTIME_ODEX";
    private static final String KEY_RUNTIME_LIB = "KEY_RUNTIME_LIB";

    /**
     * 加载runtime apk
     *
     * @return true 加载了新的runtime
     */
    public static boolean loadRuntime(InstalledApk installedRuntimeApk) {
        ClassLoader contextClassLoader = DynamicRuntime.class.getClassLoader();
        ClassLoader parent = contextClassLoader.getParent();
        // TODO cubershi: 2018-12-27 这里不应该默认contextClassLoader.getParent()就是我们的ClassLoader，别人也有可能改。
        if (parent instanceof DexPathClassLoader) {
            String apkPath = ((DexPathClassLoader) parent).apkPath;
            if (mLogger.isInfoEnabled()) {
                mLogger.info("last apkPath:" + apkPath + " new apkPath:" + installedRuntimeApk.apkFilePath);
            }
            if (TextUtils.equals(apkPath, installedRuntimeApk.apkFilePath)) {
                //已经加载相同版本的runtime了,不需要加载
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("已经加载相同apkPath的runtime了,不需要加载");
                }
                return false;
            } else {
                //版本不一样，说明要更新runtime，先恢复正常的classLoader结构
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("加载不相同apkPath的runtime了,更新runtime");
                }
                try {
                    hackParentClassLoader(contextClassLoader, parent.getParent());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        //正常处理，将runtime 挂到pathclassLoader之上
        try {
            hackParentToRuntime(installedRuntimeApk, contextClassLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private static void hackParentToRuntime(InstalledApk installedRuntimeApk, ClassLoader contextClassLoader) throws Exception {
        DexPathClassLoader pluginContainerClassLoader = new DexPathClassLoader(installedRuntimeApk.apkFilePath, installedRuntimeApk.oDexPath,
                installedRuntimeApk.libraryPath, contextClassLoader.getParent());
        hackParentClassLoader(contextClassLoader, pluginContainerClassLoader);
    }


    /**
     * 修改ClassLoader的parent
     *
     * @param classLoader          需要修改的ClassLoader
     * @param newParentClassLoader classLoader的新的parent
     * @throws Exception 失败时抛出
     */
    private static void hackParentClassLoader(ClassLoader classLoader,
                                              ClassLoader newParentClassLoader) throws Exception {
        Field field = getParentField();
        if (field == null) {
            throw new RuntimeException("在ClassLoader.class中没找到类型为ClassLoader的parent域");
        }
        field.setAccessible(true);
        field.set(classLoader, newParentClassLoader);
    }

    /**
     * 安全地获取到ClassLoader类的parent域
     *
     * @return ClassLoader类的parent域.或不能通过反射访问该域时返回null.
     */
    private static Field getParentField() {
        ClassLoader classLoader = DynamicRuntime.class.getClassLoader();
        ClassLoader parent = classLoader.getParent();
        Field field = null;
        for (Field f : ClassLoader.class.getDeclaredFields()) {
            try {
                boolean accessible = f.isAccessible();
                f.setAccessible(true);
                Object o = f.get(classLoader);
                f.setAccessible(accessible);
                if (o == parent) {
                    field = f;
                    break;
                }
            } catch (IllegalAccessException ignore) {
            }
        }
        return field;
    }

    /**
     * 重新恢复runtime
     *
     * @return true 进行了runtime恢复
     */
    public static boolean recoveryRuntime(Context context) {
        InstalledApk installedApk = getLastRuntimeInfo(context);
        if (installedApk != null && new File(installedApk.apkFilePath).exists()) {
            if (installedApk.oDexPath != null && !new File(installedApk.oDexPath).exists()) {
                return false;
            }
            try {
                hackParentToRuntime(installedApk, DynamicRuntime.class.getClassLoader());
                return true;
            } catch (Exception e) {
                if (mLogger.isErrorEnabled()) {
                    mLogger.error("recoveryRuntime 错误", e);
                }
                removeLastRuntimeInfo(context);
            }
        }
        return false;
    }

    public static void saveLastRuntimeInfo(Context context, InstalledApk installedRuntimeApk) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putString(KEY_RUNTIME_APK, installedRuntimeApk.apkFilePath)
                .putString(KEY_RUNTIME_ODEX, installedRuntimeApk.oDexPath)
                .putString(KEY_RUNTIME_LIB, installedRuntimeApk.libraryPath)
                .apply();
    }

    private static InstalledApk getLastRuntimeInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String apkFilePath = preferences.getString(KEY_RUNTIME_APK, null);
        String oDexPath = preferences.getString(KEY_RUNTIME_ODEX, null);
        String libraryPath = preferences.getString(KEY_RUNTIME_LIB, null);

        if (apkFilePath == null) {
            return null;
        } else {
            return new InstalledApk(apkFilePath, oDexPath, libraryPath);
        }
    }

    private static void removeLastRuntimeInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .remove(KEY_RUNTIME_APK)
                .remove(KEY_RUNTIME_ODEX)
                .remove(KEY_RUNTIME_LIB)
                .apply();
    }


    static class DexPathClassLoader extends BaseDexClassLoader {
        /**
         * 加载的apk路径
         */
        private String apkPath;


        DexPathClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
            super(dexPath, optimizedDirectory == null ? null : new File(optimizedDirectory), librarySearchPath, parent);
            this.apkPath = dexPath;
        }
    }
}
