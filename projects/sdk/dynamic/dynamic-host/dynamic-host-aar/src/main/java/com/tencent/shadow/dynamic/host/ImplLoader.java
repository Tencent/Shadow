package com.tencent.shadow.dynamic.host;

import com.tencent.shadow.core.common.InstalledApk;

import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;

abstract class ImplLoader {
    private static final String WHITE_LIST_CLASS_NAME = "com.tencent.shadow.dynamic.impl.WhiteList";
    private static final String WHITE_LIST_FIELD_NAME = "sWhiteList";

    abstract String[] getCustomWhiteList();

    String[] loadWhiteList(InstalledApk installedApk) {
        DexClassLoader dexClassLoader = new DexClassLoader(
                installedApk.apkFilePath,
                installedApk.oDexPath,
                installedApk.libraryPath,
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
            interfaces = concatenate(getCustomWhiteList(), whiteList);
        } else {
            interfaces = getCustomWhiteList();
        }
        return interfaces;
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
