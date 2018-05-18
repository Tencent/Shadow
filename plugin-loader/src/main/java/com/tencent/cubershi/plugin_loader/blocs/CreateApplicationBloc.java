package com.tencent.cubershi.plugin_loader.blocs;

import com.tencent.cubershi.plugin_loader.exceptions.CreateApplicationException;

import java.lang.reflect.Method;

/**
 * 初始化插件Application类
 *
 * @author cubershi
 */
public class CreateApplicationBloc {
    public static Object callPluginApplicationOnCreate(ClassLoader pluginClassLoader, String appClassName) throws CreateApplicationException {
        try {
            final Class<?> appClass = pluginClassLoader.loadClass(appClassName);
            final Class<?> mockApplicationClass = getMockApplicationClass(pluginClassLoader);
            final Object mockApplication = mockApplicationClass.cast(appClass.newInstance());
            final Method onCreate = mockApplicationClass.getDeclaredMethod("onCreate", null);
            onCreate.invoke(mockApplication, null);
            return mockApplication;
        } catch (Exception e) {
            throw new CreateApplicationException(e);
        }
    }

    private static Class<?> getMockApplicationClass(ClassLoader classLoader) throws CreateApplicationException {
        try {
            return classLoader.loadClass("android.app.Application");
        } catch (ClassNotFoundException e) {
            throw new CreateApplicationException(e);
        }
    }
}
