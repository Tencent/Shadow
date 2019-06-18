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

package com.tencent.shadow.dynamic.host;

import java.io.File;
import java.util.concurrent.Future;

/**
 * PluginManager文件升级器
 * <p>
 * 注意这个类不负责什么时候该升级PluginManager，
 * 它只提供需要升级时的功能，如下载和向远端查询文件是否还可用。
 */
public interface PluginManagerUpdater {
    /**
     * @return <code>true</code>表示之前更新过程中意外中断了
     */
    boolean wasUpdating();

    /**
     * 更新
     *
     * @return 当前最新的PluginManager，可能是之前已经返回过的文件，但它是最新的了。
     */
    Future<File> update();

    /**
     * 获取本地最新可用的
     *
     * @return <code>null</code>表示本地没有可用的
     */
    File getLatest();

    /**
     * 查询是否可用
     *
     * @param file PluginManagerUpdater返回的file
     * @return <code>true</code>表示可用，<code>false</code>表示不可用
     */
    Future<Boolean> isAvailable(File file);
}
