package com.tencent.cubershi.plugin_loader.blocs;

import android.app.Application;

import com.tencent.cubershi.plugin_loader.exceptions.CreateApplicationException;

/**
 * 初始化插件Application类
 *
 * @author cubershi
 */
public class CreateApplicationBloc {
    public static Application callPluginApplicationOnCreate(ClassLoader pluginClassLoader, String appClassName) throws CreateApplicationException {
        try {
            final Class<?> appClass = pluginClassLoader.loadClass(appClassName);
            final Application mockApplication = Application.class.cast(appClass.newInstance());
            mockApplication.onCreate();
            return mockApplication;
        } catch (Exception e) {
            throw new CreateApplicationException(e);
        }
    }
}
