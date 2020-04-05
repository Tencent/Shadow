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

package com.tencent.shadow.core.runtime.container;


import android.os.SystemClock;

import java.util.HashMap;
import java.util.Map;

/**
 * DelegateProvider依赖注入类
 * <p>
 * dynamic-pluginloader通过这个类实现将PluginLoader中的DelegateProvider实现注入到plugincontainer中。
 *
 * @author cubershi
 */
public class DelegateProviderHolder {
    public static final String DEFAULT_KEY = "DEFAULT_KEY";
    private static Map<String, DelegateProvider> delegateProviderMap = new HashMap<>();

    /**
     * 为了防止系统有一定概率出现进程号重启后一致的问题，我们使用开机时间作为进程号来判断进程是否重启
     */
    public static long sCustomPid ;

    static {
        sCustomPid = SystemClock.elapsedRealtime();
    }

    public static void setDelegateProvider(String key, DelegateProvider delegateProvider) {
        delegateProviderMap.put(key, delegateProvider);
    }

    public static DelegateProvider getDelegateProvider(String key) {
        return delegateProviderMap.get(key);
    }
}
