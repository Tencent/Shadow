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

package com.tencent.shadow.core.runtime.remoteview;

import android.view.View;

/**
 * 跨插件apk创建View结果回调接口
 * Created by jaylanchen on 2018/12/7.
 */
public interface ShadowRemoteViewCreateCallback {

    /**
     * view创建成功
     * @param view 创建好的View
     */
    void onViewCreateSuccess(View view);

    /**
     * view创建失败
     * @param failInfo 失败原因
     */
    void onViewCreateFailed(Exception failInfo);

}
