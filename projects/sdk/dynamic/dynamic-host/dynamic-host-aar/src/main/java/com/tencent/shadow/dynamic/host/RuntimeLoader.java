package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.tencent.shadow.core.interface_.log.ILogger;
import com.tencent.shadow.core.interface_.log.ShadowLoggerFactory;

import java.io.File;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;

/**
 * 将runtime apk加载到DexPathClassLoader，形成如下结构的classLoader树结构
 * ---BootClassLoader
 * ----DexPathClassLoader
 * ------PathClassLoader
 */
public class RuntimeLoader {

    private static ILogger mLogger = ShadowLoggerFactory.getLogger("shadow::RuntimeLoader");

    private static String SP_NAME = "ShadowRuntimeLoader";

    private static String KEY_CONTAINER = "key_RuntimeInfo";

    /**
     * 加载runtime apk
     * @return true 加载了新的runtime
     */
    public static boolean loadRuntime(RuntimeInfo runtimeInfo) {
        ClassLoader contextClassLoader = RuntimeLoader.class.getClassLoader();
        ClassLoader parent = contextClassLoader.getParent();
        if (parent instanceof DexPathClassLoader) {
            String apkPath = ((DexPathClassLoader) parent).apkPath;
            if (mLogger.isInfoEnabled()) {
                mLogger.info("last apkPath:" + apkPath + " new apkPath:" + runtimeInfo.apkPath);
            }
            if (TextUtils.equals(apkPath, runtimeInfo.apkPath)) {
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
            DexPathClassLoader pluginContainerClassLoader = new DexPathClassLoader(runtimeInfo.apkPath, runtimeInfo.oDexPath,
                    runtimeInfo.libraryPath, contextClassLoader.getParent());
            hackParentClassLoader(contextClassLoader, pluginContainerClassLoader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
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
        ClassLoader classLoader = RuntimeLoader.class.getClassLoader();
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
        String json = getLastRuntimeInfo(context);
        if (json != null) {
            RuntimeInfo runtimeInfo = new RuntimeInfo(json);
            new DexPathClassLoader(runtimeInfo.apkPath, runtimeInfo.oDexPath,
                    runtimeInfo.libraryPath, RuntimeLoader.class.getClassLoader().getParent());
            return true;
        }
        return false;
    }

    public static void saveLastRuntimeInfo(Context context, RuntimeInfo runtimeInfo) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_CONTAINER, runtimeInfo.toJson().toString()).apply();
    }

    private static String getLastRuntimeInfo(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_CONTAINER, null);
    }


    static class DexPathClassLoader extends BaseDexClassLoader {
        /**
         * 加载的apk路径
         */
        public String apkPath;


        public DexPathClassLoader(String dexPath, String optimizedDirectory, String librarySearchPath, ClassLoader parent) {
            super(dexPath, optimizedDirectory == null ? null : new File(optimizedDirectory), librarySearchPath, parent);
            this.apkPath = dexPath;
        }
    }
}
