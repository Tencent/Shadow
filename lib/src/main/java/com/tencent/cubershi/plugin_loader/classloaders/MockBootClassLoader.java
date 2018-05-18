package com.tencent.cubershi.plugin_loader.classloaders;

import com.tencent.cubershi.plugin_loader.mocks.MockApplication;

/**
 * 模仿BootClassLoader提供Android组件class的ClassLoader
 *
 * @author cubershi
 */
public class MockBootClassLoader {
    Class<?> findClass(String name) throws ClassNotFoundException {
        switch (name) {
            case "android.app.Application":
                return MockApplication.class;
            default:
                throw new ClassNotFoundException(name);
        }
    }
}
