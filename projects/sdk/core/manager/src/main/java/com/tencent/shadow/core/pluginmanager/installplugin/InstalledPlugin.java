package com.tencent.shadow.core.pluginmanager.installplugin;


import com.tencent.shadow.core.host.API;

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
    public Map<String, Part> interfaces = new HashMap<>();


    public InstalledPlugin() {
    }


    public PartInfo getPartInfo(String partKey) {
        Part part = plugins.get(partKey);
        PartInfo partInfo = null;
        if (part != null) {
            partInfo = new PartInfo(part.file.getAbsolutePath(), false);
        } else {
            part = interfaces.get(partKey);
            if (part != null) {
                partInfo = new PartInfo(part.file.getAbsolutePath(), true);
            }
        }
        return partInfo;
    }

    static public class Part implements Serializable {
        final public File file;

        Part(File file) {
            this.file = file;
        }
    }

    static class PluginPart extends Part {
        final String[] dependsOn;

        PluginPart(File file, String[] dependsOn) {
            super(file);
            this.dependsOn = dependsOn;
        }
    }
}
