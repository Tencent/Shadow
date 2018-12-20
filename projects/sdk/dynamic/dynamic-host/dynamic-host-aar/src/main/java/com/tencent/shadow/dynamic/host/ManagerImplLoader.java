package com.tencent.shadow.dynamic.host;

import android.content.Context;

import com.tencent.shadow.core.interface_.PluginManager;

import java.io.File;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

final class ManagerImplLoader {
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
        File odexDir = new File(root, Long.toString(apk.lastModified(), Character.MAX_RADIX));
        odexDir.mkdirs();

        DexClassLoader dexClassLoader = new DexClassLoader(
                apk.getAbsolutePath(),
                odexDir.getAbsolutePath(),
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
                odexDir.getAbsolutePath(),
                null,
                getClass().getClassLoader(),
                interfaces);

        Context pluginManagerContext = new ChangeApkContextWrapper(
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

    private static String[] concatenate(String[] a, String[] b) {
        int aLen = a.length;
        int bLen = b.length;
        String[] c = new String[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
