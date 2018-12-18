package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.Build;

import com.tencent.shadow.core.interface_.PluginManager;
import com.tencent.shadow.core.interface_.log.ILogger;
import com.tencent.shadow.core.interface_.log.ShadowLoggerFactory;

import java.io.File;
import java.lang.reflect.Field;

import dalvik.system.BaseDexClassLoader;

final class ManagerImplLoader {
    private ILogger mLogger = ShadowLoggerFactory.getLogger("shadow::ManagerImplLoader");
    private static final String MANAGER_FACTORY_CLASS_NAME = "com.tencent.shadow.dynamic.impl.ManagerFactoryImpl";
    private static final String WHITE_LIST_CLASS_NAME = "com.tencent.shadow.dynamic.impl.WhiteList";
    private static final String WHITE_LIST_FIELD_NAME = "sWhiteList";
    private static final String[] REMOTE_PLUGIN_MANAGER_INTERFACES = new String[]
            {
                    "com.tencent.shadow.core.interface_",
                    "com.tencent.shadow.core.interface_.log",
                    "com.tencent.shadow.dynamic.host",
            };
    final private File apk;
    final private Context applicationContext;

    ManagerImplLoader(Context context, File apk) {
        applicationContext = context.getApplicationContext();
        this.apk = apk;
    }

    PluginManager load() {
        File root = new File(applicationContext.getFilesDir(), "ManagerImplLoader");
        File odexDir = null;
        if (Build.VERSION.SDK_INT < 21) { //只有4.4及以下的手机才odex优化
            odexDir = new File(root, Long.toString(apk.lastModified(), Character.MAX_RADIX));
            odexDir.mkdirs();
        }
        if (mLogger.isInfoEnabled()) {
            mLogger.info("load SDK_INT:" + Build.VERSION.SDK_INT);
        }
        BaseDexClassLoader dexClassLoader = new BaseDexClassLoader(
                apk.getAbsolutePath(),
                odexDir,
                null,
                getClass().getClassLoader()
        );

        String[] whiteList = null;
        try {
            Class<?> whiteListClass = dexClassLoader.loadClass(WHITE_LIST_CLASS_NAME);
            Field whiteListField = whiteListClass.getDeclaredField(WHITE_LIST_FIELD_NAME);
            Object o = whiteListField.get(null);
            whiteList = (String[]) o;
        } catch (ClassNotFoundException ignored) {
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        String[] interfaces;
        if (whiteList != null) {
            interfaces = concatenate(REMOTE_PLUGIN_MANAGER_INTERFACES, whiteList);
        } else {
            interfaces = REMOTE_PLUGIN_MANAGER_INTERFACES;
        }

        ApkClassLoader apkClassLoader = new ApkClassLoader(
                apk.getAbsolutePath(),
                odexDir == null ? null : odexDir.getAbsolutePath(),
                null,
                getClass().getClassLoader(),
                interfaces);

        Context pluginManagerContext = new PluginManagerContext(
                applicationContext,
                apk.getAbsolutePath(),
                apkClassLoader
        );

        try {
            ManagerFactory managerFactory = apkClassLoader.getInterface(
                    ManagerFactory.class,
                    MANAGER_FACTORY_CLASS_NAME
            );
            return managerFactory.buildManager(pluginManagerContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] concatenate(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] c = new String[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
