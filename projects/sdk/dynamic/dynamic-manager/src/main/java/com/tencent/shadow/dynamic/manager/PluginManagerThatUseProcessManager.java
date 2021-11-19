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
package com.tencent.shadow.dynamic.manager;

import android.content.Context;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.dynamic.host.FailedException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

abstract public class PluginManagerThatUseProcessManager extends BaseDynamicPluginManager {
    private static final Logger mLogger = LoggerFactory.getLogger(PluginManagerThatUseProcessManager.class);

    private ProcessManager processManager;

    public PluginManagerThatUseProcessManager(Context context) {
        super(context);
        processManager = new ProcessManager(mHostContext, getPluginProcessServiceNames(), this);
    }

    public void initProcessLoaderSync(String uuid, int timeout, TimeUnit timeUnit) throws FailedException, TimeoutException {
        processManager.initProcessLoaderSync(uuid, timeout, timeUnit);
    }

    public ProcessLoader getProcessLoader(String uuid) {
        return processManager.getProcessLoader(uuid);
    }

    /**
     * 一个Service对应一个进程
     *
     * @return 插件进程 注册在宿主中的Service全限定名。
     */
    abstract protected String[] getPluginProcessServiceNames();
}
