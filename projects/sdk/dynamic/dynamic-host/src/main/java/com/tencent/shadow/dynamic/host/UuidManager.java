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

import android.os.RemoteException;

import com.tencent.shadow.core.common.InstalledApk;

public interface UuidManager {

    int TRANSACTION_CODE_NO_EXCEPTION = 0;
    int TRANSACTION_CODE_FAILED_EXCEPTION = 1;
    int TRANSACTION_CODE_NOT_FOUND_EXCEPTION = 2;
    String DESCRIPTOR = UuidManager.class.getName();
    int TRANSACTION_getPlugin = (android.os.IBinder.FIRST_CALL_TRANSACTION);
    int TRANSACTION_getPluginLoader = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    int TRANSACTION_getRuntime = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);

    InstalledApk getPlugin(String uuid, String partKey) throws RemoteException, NotFoundException, FailedException;

    InstalledApk getPluginLoader(String uuid) throws RemoteException, NotFoundException, FailedException;

    InstalledApk getRuntime(String uuid) throws RemoteException, NotFoundException, FailedException;
}
