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

import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class ShadowPackageItemInfo {

    /**
     * @param classLoader 对应插件所在的classLoader
     * @param packageItemInfo MetaData所在的组件
     * @param pm PackageManager
     * @param name metaData对应的name
     * @return  返回所在插件的xml对应的XmlResourceParser
     */
    public static XmlResourceParser loadXmlMetaData(ClassLoader classLoader, PackageItemInfo packageItemInfo, PackageManager pm, String name) {
        PluginPartInfo pluginPartInfo = PluginPartInfoManager.getPluginInfo(classLoader);
        Resources resources = pluginPartInfo.application.getResources();
        if (packageItemInfo.metaData != null) {
            int resid = packageItemInfo.metaData.getInt(name);
            if (resid != 0) {
                return resources.getXml(resid);
            }
        }
        return null;
    }

}
