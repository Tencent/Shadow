package com.tencent.shadow.core.pluginmanager.installplugin;


import com.tencent.shadow.core.interface_.API;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 已安装好的插件.
 * <p>
 * 这是一个Serializable类，目的是可以将这个类的对象放在Intent中跨进程传递。
 * 注意：equals()方法必须重载，并包含全部域变量。
 *
 * @author owenguo
 */
@API
public class InstalledPlugin implements Serializable {

    /**
     * 标识一次插件发布的id
     */
    public String UUID;
    /**
     * 标识一次插件发布的id，可以使用自定义格式描述版本信息
     */
    public String UUID_NickName;

    /**
     * pluginLoader文件
     */
    public Part pluginLoaderFile;

    /**
     * runtime文件
     */
    public Part runtimeFile;
    /**
     * 插件文件
     */
    public Map<String, PluginPart> plugins = new HashMap<>();
    /**
     * 接口文件
     */
    public Map<String, PluginPart> interfaces = new HashMap<>();

    /**
     * 插件的存储目录
     */
    public File storageDir;


    public InstalledPlugin() {
    }


    public boolean hasPart(String partKey) {
        return plugins.containsKey(partKey) || interfaces.containsKey(partKey);
    }

    public boolean isInterface(String partKey) {
        return interfaces.containsKey(partKey);
    }

    public PluginPart getInterface(String partKey) {
        return interfaces.get(partKey);
    }

    public PluginPart getPlugin(String partKey) {
        return plugins.get(partKey);
    }

    public Part getPart(String partKey) {
        Part part = plugins.get(partKey);
        if (part == null) {
            part = interfaces.get(partKey);
        }
        return part;
    }

    static public class Part implements Serializable {
        final public File pluginFile;


        Part(File pluginFile) {
            this.pluginFile = pluginFile;

        }
    }

    static public class PluginPart extends Part {
        final public String[] dependsOn;
        final public File oDexDir;
        final public File libraryDir;

        PluginPart(File file, File oDexDir, File libraryDir, String[] dependsOn) {
            super(file);
            this.oDexDir = oDexDir;
            this.libraryDir = libraryDir;
            this.dependsOn = dependsOn;
        }
    }
}
