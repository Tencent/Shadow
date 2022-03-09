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

import android.content.Context;
import android.os.Bundle;

/**
 * 使用方持有的接口
 *
 * @author cubershi
 */
public interface PluginManager {

    /**
     * @param context  context
     * @param fromId   标识本次请求的来源位置，用于区分入口
     * @param bundle   参数列表
     * @param callback 用于从PluginManager实现中返回View
     */
    void enter(Context context, long fromId, Bundle bundle, EnterCallback callback);
}
