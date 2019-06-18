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

package com.tencent.shadow.sample.plugin.app.lib.gallery.util;


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
