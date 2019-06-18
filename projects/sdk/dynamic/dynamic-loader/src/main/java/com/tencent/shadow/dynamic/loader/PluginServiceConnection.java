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

/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/shifujun/Codes/Android/shadow/projects/sdk/dynamic/dynamic-loader/dynamic-loader-aar/src/main/aidl/com/tencent/shadow/dynamic/loader/IServiceConnection.aidl
 */
package com.tencent.shadow.dynamic.loader;

import android.content.ComponentName;
import android.os.IBinder;

public interface PluginServiceConnection {
    String DESCRIPTOR = PluginServiceConnection.class.getName();
    int TRANSACTION_onServiceConnected = IBinder.FIRST_CALL_TRANSACTION;
    int TRANSACTION_onServiceDisconnected = IBinder.FIRST_CALL_TRANSACTION + 1;

    void onServiceConnected(ComponentName name, IBinder service);

    void onServiceDisconnected(ComponentName name);

}
