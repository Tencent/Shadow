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

package com.tencent.shadow.core.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PluginPartInfoManager {

    private static Map<ClassLoader, PluginPartInfo> sPluginInfos = new HashMap<>();

    public static void addPluginInfo(ClassLoader classLoader, PluginPartInfo pluginPartInfo) {
        sPluginInfos.put(classLoader, pluginPartInfo);
    }

    public static PluginPartInfo getPluginInfo(ClassLoader classLoader) {
        PluginPartInfo pluginPartInfo = sPluginInfos.get(classLoader);
        if (pluginPartInfo == null) {
            throw new RuntimeException("没有找到classLoader对应的pluginInfo classLoader:" + classLoader);
        }
        return pluginPartInfo;
    }


    public static Collection<PluginPartInfo> getAllPluginInfo() {
        return sPluginInfos.values();
    }


}
