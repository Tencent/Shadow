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

package com.tencent.shadow.core.runtime.container;

import android.content.pm.PackageManager;
import android.view.InputQueue;
import android.view.SurfaceHolder;

public interface HostNativeActivityDelegate extends HostActivityDelegate {

    PackageManager getPackageManager();

    void surfaceCreated(SurfaceHolder holder);

    void surfaceChanged(SurfaceHolder holder, int format, int width, int height);

    void surfaceRedrawNeeded(SurfaceHolder holder);

    void surfaceDestroyed(SurfaceHolder holder);

    void onInputQueueCreated(InputQueue queue);

    void onInputQueueDestroyed(InputQueue queue);

    void onGlobalLayout();
}
