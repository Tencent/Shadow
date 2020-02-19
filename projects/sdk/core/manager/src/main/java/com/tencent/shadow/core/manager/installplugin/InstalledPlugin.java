/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.manager.installplugin;


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


    InstalledPlugin() {
    }


    public boolean hasPart(String partKey) {
        return plugins.containsKey(partKey);
    }

    public PluginPart getPlugin(String partKey) {
        return plugins.get(partKey);
    }

    public Part getPart(String partKey) {
        return plugins.get(partKey);
    }

    static public class Part implements Serializable {
        final public int pluginType;
        final public File pluginFile;
        public File oDexDir;
        public File libraryDir;

        Part(int pluginType, File file, File oDexDir, File libraryDir) {
            this.pluginType = pluginType;
            this.oDexDir = oDexDir;
            this.libraryDir = libraryDir;
            this.pluginFile = file;
        }
    }

    static public class PluginPart extends Part {
        final public String businessName;
        final public String[] dependsOn;
        final public String[] hostWhiteList;

        PluginPart(int pluginType, String businessName, File file, File oDexDir, File libraryDir, String[] dependsOn, String[] hostWhiteList) {
            super(pluginType, file, oDexDir, libraryDir);
            this.businessName = businessName;
            this.dependsOn = dependsOn;
            this.hostWhiteList = hostWhiteList;
        }
    }
}
