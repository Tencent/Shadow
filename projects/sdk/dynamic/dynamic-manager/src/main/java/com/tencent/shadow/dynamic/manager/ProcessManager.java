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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 进程管理
 */
public class ProcessManager {
    private static final Logger mLogger = LoggerFactory.getLogger(ProcessManager.class);

    private final String[] serviceNames;

    /**
     * 用来检测Service是否已经被占用
     * key:     serviceName
     * value:   {@link ProcessLoader}
     */
    private final HashMap<String, ProcessLoader> serviceUsedMap = new HashMap<>();

    /**
     * 用于快速加载uuid所使用的loader
     * key:     uuid
     * value:   serviceName
     */
    private final HashMap<String, String> uuidUsedMap = new HashMap<>();

    /**
     * loader缓存
     */
    private final LinkedList<ProcessLoader> processLoaderCache = new LinkedList<>();

    private final Context hostContext;

    private final UuidManagerImpl uuidManager;

    public ProcessManager(Context hostContext,
                          String[] serviceNames,
                          UuidManagerImpl uuidManager) {
        this.hostContext = hostContext;
        this.serviceNames = serviceNames == null ? new String[]{} : serviceNames;
        this.uuidManager = uuidManager;
        mLogger.info("ProcessManager init");
    }

    public ProcessLoader getProcessLoader(String uuid) {
        String serviceName = uuidUsedMap.get(uuid);
        if (serviceName == null) {
            mLogger.info("getProcessLoader uuid={}, serviceName is null", uuid);
            return null;
        }
        ProcessLoader processLoader = serviceUsedMap.get(serviceName);
        mLogger.info("getProcessLoader uuid={}, serviceName={} ProcessLoader=={}", uuid, serviceName, processLoader);
        return processLoader;
    }

    public void initProcessLoaderSync(String uuid, int timeout, TimeUnit timeUnit) throws FailedException, TimeoutException {
        ProcessLoader processLoader = findOrCreate();
        String serviceName = findIdleServiceName();
        if (serviceName == null) {
            throw new FailedException(-1, "没有找到空闲的进程");
        }

        processLoader.bindPluginProcessService(serviceName);
        processLoader.waitServiceConnected(timeout, timeUnit);
        mLogger.info("进程已占用 uuid = {}, serviceName = {}", uuid, serviceName);
        serviceUsedMap.put(serviceName, processLoader);
        uuidUsedMap.put(uuid, serviceName);
    }

    private String findIdleServiceName() {
        ProcessLoader processLoader;
        for (String serviceName : serviceNames) {
            processLoader = serviceUsedMap.get(serviceName);
            if (processLoader == null) {
                mLogger.info("ProcessLoader未创建过，获取到一个空闲进程 serviceName={}", serviceName);
                return serviceName;
            } else if (processLoader.getPpsController() == null) {
                mLogger.info("ProcessLoader创建过，但是不再活着了 serviceName={}", serviceName);
                serviceUsedMap.remove(serviceName);
                cacheProcessLoader(processLoader);
                return serviceName;
            }
        }
        return null;
    }

    private ProcessLoader findOrCreate() {
        if (processLoaderCache.size() > 0) {
            ProcessLoader processLoader = processLoaderCache.removeFirst();
            mLogger.info("ProcessLoader 从缓存中获取");
            return processLoader;
        }
        mLogger.info("创建了一个全新的ProcessLoader");
        return new ProcessLoader(hostContext, uuidManager);
    }

    private void cacheProcessLoader(ProcessLoader processLoader) {
        mLogger.info("ProcessLoader 缓存到获取");
        processLoaderCache.addLast(processLoader);
    }
}
