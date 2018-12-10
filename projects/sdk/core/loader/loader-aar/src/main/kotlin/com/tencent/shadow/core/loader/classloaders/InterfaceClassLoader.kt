package com.tencent.shadow.core.loader.classloaders

import android.os.Build

/**
 * 公用接口classloader,它加载插件间共用的接口，它将作为所有插件classloader的
 * 父classloader
 * Created by jaylanchen on 2018/12/5.
 */
class InterfaceClassLoader(parent: ClassLoader) : ClassLoader(parent) {

    private val mGrandParent = parent.parent
    private val mInterfaceClassLoaderList = ArrayList<ClassLoader>()


    @Synchronized
    fun addInterfaceClassLoader(classLoader: ClassLoader) {
        mInterfaceClassLoaderList.add(classLoader)
    }

    override fun loadClass(className: String, resolve: Boolean): Class<*> {
        if (className.startsWith("com.tencent.shadow.runtime")
                || className.startsWith("org.apache.commons.logging")//org.apache.commons.logging是非常特殊的的包,由系统放到App的PathClassLoader中.
                || (Build.VERSION.SDK_INT < 28 && className.startsWith("org.apache.http"))) {//Android 9.0以下的系统里面带有http包，走系统的不走本地的
            return super.loadClass(className, resolve)
        } else {

            var clazz: Class<*>? = null

            for (classLoader in mInterfaceClassLoaderList) {
                try {
                    clazz = classLoader.loadClass(className)
                    if (clazz != null) {
                        break
                    }
                } catch (e: ClassNotFoundException) {

                }
            }


            if (clazz == null) {
                try {
                    clazz = mGrandParent.loadClass(className)
                } catch (e: ClassNotFoundException) {
                }
            }

            if (clazz == null) {
                // 还是找不到
                throw ClassNotFoundException(className)
            }

            return clazz
        }

    }

}