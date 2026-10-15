/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 */
package com.tencent.shadow.core.runtime.container;

import android.os.SystemClock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按Loader key持有PluginContainerServiceDelegate，支持同进程多Loader。
 */
public class PluginContainerServiceDelegateHolder {

    public static final long PROCESS_ID = SystemClock.elapsedRealtime();

    private static final Map<String, PluginContainerServiceDelegate> DELEGATES =
            new ConcurrentHashMap<>();

    public static void setDelegate(String key, PluginContainerServiceDelegate delegate) {
        DELEGATES.put(key, delegate);
    }

    public static PluginContainerServiceDelegate getDelegate(String key) {
        return DELEGATES.get(key);
    }
}
