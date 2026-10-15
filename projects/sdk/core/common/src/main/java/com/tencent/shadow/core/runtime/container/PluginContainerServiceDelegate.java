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

import android.content.Intent;

/**
 * PluginContainerService的委托接口，由loader层PluginServiceManager实现。
 */
public interface PluginContainerServiceDelegate {

    int onStartCommand(PluginContainerService containerService, Intent intent, int flags, int startId);

    void onDestroy(PluginContainerService containerService);
}
