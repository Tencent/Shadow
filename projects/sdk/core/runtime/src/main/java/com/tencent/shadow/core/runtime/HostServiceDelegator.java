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
package com.tencent.shadow.core.runtime;

import android.app.Notification;

/**
 * 前台Service代理接口。
 * 由PluginServiceManager创建的聚合代理实现，不直通容器Service。
 * 只暴露前台状态管理方法，stopSelf由ShadowService走stopService统一路径。
 */
public interface HostServiceDelegator {

    void startForeground(int id, Notification notification);

    void startForeground(int id, Notification notification, int foregroundServiceType);

    void stopForeground(boolean removeNotification);

    void stopForeground(int flags);
}
