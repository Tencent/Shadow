package com.tencent.shadow.sdk.pluginmanager.installplugin;


import com.tencent.shadow.core.host.common.annotation.API;

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
    public File pluginLoaderFile;

    /**
     * runtime文件
     */
    public File runtimeFile;
    /**
     * 插件文件
     */
    public Map<String, File> plugins = new HashMap<>();
    /**
     * 接口文件
     */
    public Map<String, File> interfaces = new HashMap<>();


    public InstalledPlugin() {
    }


    public PartInfo getPartInfo(String partKey) {
        File file = plugins.get(partKey);
        PartInfo partInfo = null;
        if (file != null) {
            partInfo = new PartInfo(file.getAbsolutePath(), false);
        } else {
            file = interfaces.get(partKey);
            if (file != null) {
                partInfo = new PartInfo(file.getAbsolutePath(), true);
            }
        }
        return partInfo;
    }

}
