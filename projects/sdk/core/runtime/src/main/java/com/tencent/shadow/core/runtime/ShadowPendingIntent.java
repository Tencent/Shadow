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

package com.tencent.shadow.core.runtime;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


public class ShadowPendingIntent {

    public static PendingIntent getService(Context context, int requestCode,
                                           Intent intent, int flags) {
        //todo #51 实现PendingIntent 中的 Service和广播
        return PendingIntent.getService(context, requestCode, intent, flags);
    }

    public static PendingIntent getActivity(Context context, int requestCode,
                                            Intent intent, int flags) {
        return getActivity(context, requestCode, intent, flags, null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static PendingIntent getActivity(Context context, int requestCode,
                                            Intent intent, int flags, Bundle options) {
        if (context instanceof ShadowContext && intent.getComponent() != null) {
            ShadowContext shadowContext = (ShadowContext) context;
            if (shadowContext.getPendingIntentConverter() != null) {
                intent = shadowContext.getPendingIntentConverter().convertPluginActivityIntent(intent);
            }
            context = shadowContext.getBaseContext();
        }
        return PendingIntent.getActivity(context, requestCode, intent, flags, options);
    }


}
