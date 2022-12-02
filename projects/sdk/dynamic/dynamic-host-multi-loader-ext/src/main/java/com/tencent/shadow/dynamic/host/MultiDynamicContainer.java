package com.tencent.shadow.dynamic.host;

import android.text.TextUtils;

import com.tencent.shadow.core.common.InstalledApk;
import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;

import dalvik.system.BaseDexClassLoader;

/**
 * 将Container部分的hack到PathClassLoader之上，形成如下结构的classLoader树结构
 * ---BootClassLoader
 * ----ContainerClassLoader (可能有多个)
 * -----PathClassLoader
 */
public class MultiDynamicContainer {
    private static final Logger mLogger = LoggerFactory.getLogger(MultiDynamicContainer.class);

    /**
     * hack ContainerClassLoader到PathClassLoader之上
     * 1. ClassLoader树结构中可能包含多个ContainerClassLoader
     * 2. 在hack时，需要提供containerKey作为该插件containerApk的标识
     *
     * @param containerKey 插件业务对应的key，不随插件版本变动
     * @param containerApk 插件zip包中的runtimeApk
     */
    public static boolean loadContainerApk(String containerKey, InstalledApk containerApk) {
        // 根据key去查找对应的ContainerClassLoader
        ContainerClassLoader containerClassLoader = findContainerClassLoader(containerKey);
        if (containerClassLoader != null) {
            String apkFilePath = containerClassLoader.apkFilePath;
            if (mLogger.isInfoEnabled()) {
                mLogger.info("该containKey的apk已经加载过, containKey=" + containerKey +
                        ", last apkPath=" + apkFilePath + ", new apkPath=" + containerApk.apkFilePath);
            }

            if (TextUtils.equals(apkFilePath, containerApk.apkFilePath)) {
                //已经加载相同版本的containerApk了,不需要加载
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("已经加载相同apkPath的containerApk了,不需要加载");
                }
                return false;
            } else {
                // 同个插件的ContainerClassLoader版本不一样，说明要移除老的ContainerClassLoader，插入新的
                if (mLogger.isInfoEnabled()) {
                    mLogger.info("加载不相同apkPath的containerApk了,先将老的移除");
                }
                try {
                    removeContainerClassLoader(containerClassLoader);
                } catch (Exception e) {
                    mLogger.error("移除老的containerApk失败", e);
                    throw new RuntimeException(e);
                }
            }
        }
        // 将ContainerClassLoader hack到PathClassloader之上
        try {
            hackContainerClassLoader(containerKey, containerApk);
            if (mLogger.isInfoEnabled()) {
                mLogger.info("containerApk插入成功，containerKey=" + containerKey + ", path=" + containerApk.apkFilePath);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private static ContainerClassLoader findContainerClassLoader(String containerKey) {
        ClassLoader current = MultiDynamicContainer.class.getClassLoader();
        ClassLoader parent = current.getParent();
        while (parent != null) {
            if (parent instanceof ContainerClassLoader) {
                ContainerClassLoader item = (ContainerClassLoader) parent;
                if (TextUtils.equals(item.containerKey, containerKey)) {
                    return item;
                }
            }
            parent = parent.getParent();
        }
        return null;
    }

    private static void removeContainerClassLoader(ContainerClassLoader containerClassLoader) throws Exception {
        ClassLoader pathClassLoader = MultiDynamicContainer.class.getClassLoader();
        ClassLoader child = pathClassLoader;
        ClassLoader parent = pathClassLoader.getParent();
        while (parent != null) {
            if (parent == containerClassLoader) {
                break;
            }
            child = parent;
            parent = parent.getParent();
        }
        if (child != null && parent == containerClassLoader) {
            DynamicRuntime.hackParentClassLoader(child, containerClassLoader.getParent());
        }
    }

    private static void hackContainerClassLoader(String containerKey, InstalledApk containerApk) throws Exception {
        ClassLoader pathClassLoader = MultiDynamicContainer.class.getClassLoader();
        ContainerClassLoader containerClassLoader = new ContainerClassLoader(containerKey, containerApk, pathClassLoader.getParent());
        DynamicRuntime.hackParentClassLoader(pathClassLoader, containerClassLoader);
    }

    private static class ContainerClassLoader extends BaseDexClassLoader {
        private String apkFilePath;
        private String containerKey;

        public ContainerClassLoader(String containerKey, InstalledApk installedApk, ClassLoader parent) {
            super(installedApk.apkFilePath, installedApk.oDexPath != null ? new File(installedApk.oDexPath) : null, installedApk.libraryPath, parent);
            this.containerKey = containerKey;
            this.apkFilePath = installedApk.apkFilePath;
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                // 对于当前apk中的类不进行双亲委派查找
                // 因为这个ClassLoader中的类比较特殊，Activity等壳子接口的方法上存在当前环境可能不存在的类，
                // 比如ContextParams这种API 31新引入的类。如果采用双亲委派，PluginContainerActivity等基类
                // 可能跨ClassLoader加载，会触发HasSameSignatureWithDifferentClassLoaders的校验，
                // 进而加载所有方法签名上的类型，从而可能在低版本机器上报找不到类的错误。
                try {
                    c = findClass(name);
                } catch (ClassNotFoundException ignored) {
                    c = super.loadClass(name, resolve);
                }
            }
            return c;
        }
    }
}
