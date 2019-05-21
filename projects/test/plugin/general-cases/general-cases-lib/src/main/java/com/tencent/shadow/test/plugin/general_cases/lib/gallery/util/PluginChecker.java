package com.tencent.shadow.test.plugin.general_cases.lib.gallery.util;


public class PluginChecker {

    private static Boolean sPluginMode;

    /**
     * 检测当前是否处于插件状态下
     * 这里先简单通过访问一个插件框架中的类是否成功来判断
     * @return true 是插件模式
     */
    public static boolean isPluginMode() {
        if (sPluginMode == null) {
            try {
                PluginChecker.class.getClassLoader().loadClass("com.tencent.shadow.core.runtime.ShadowApplication");
                sPluginMode = true;
            } catch (ClassNotFoundException e) {
                sPluginMode = false;
            }
        }
        return sPluginMode;
    }

}
