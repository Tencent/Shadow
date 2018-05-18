package com.tencent.cubershi.plugin_loader.blocs;

import com.tencent.cubershi.plugin_loader.exceptions.CreateApplicationException;
import com.tencent.cubershi.plugin_loader.mocks.MockApplication;

/**
 * 初始化插件Application类
 *
 * @author cubershi
 */
public class CreateApplicationBloc {
    public static MockApplication callPluginApplicationOnCreate(ClassLoader pluginClassLoader, String appClassName) throws CreateApplicationException {
        try {
            final Class<?> appClass = pluginClassLoader.loadClass(appClassName);
            return MockApplication.class.cast(appClass.newInstance());
        } catch (Exception e) {
            throw new CreateApplicationException(e);
        }
    }
}
