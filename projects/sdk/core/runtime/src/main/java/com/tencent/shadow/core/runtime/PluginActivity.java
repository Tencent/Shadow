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

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.Window;

import com.tencent.shadow.core.runtime.container.HostActivityDelegator;
import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

public abstract class PluginActivity extends GeneratedPluginActivity {

    static PluginActivity get(PluginContainerActivity pluginContainerActivity) {
        Object o = pluginContainerActivity.getPluginActivity();
        if (o != null) {
            return (PluginActivity) o;
        } else {
            //在遇到IllegalIntent时hostActivityDelegate==null。需要返回一个空的Activity避免Crash。
            return new ShadowActivity() {
            };
        }
    }

    HostActivityDelegator hostActivityDelegator;

    ShadowApplication mPluginApplication;

    ComponentName mCallingActivity;

    public final void setHostContextAsBase(Context context) {
        attachBaseContext(context);
    }

    public void setHostActivityDelegator(HostActivityDelegator delegator) {
        super.hostActivityDelegator = delegator;
        hostActivityDelegator = delegator;
    }

    public void setPluginApplication(ShadowApplication pluginApplication) {
        mPluginApplication = pluginApplication;
    }

    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_OPTIONS_PANEL) {
            return onCreateOptionsMenu(menu);
        } else {
            return hostActivityDelegator.superOnCreatePanelMenu(featureId, menu);
        }
    }

    public LayoutInflater getLayoutInflater() {
        LayoutInflater inflater = hostActivityDelegator.getWindow().getLayoutInflater();
        return ShadowLayoutInflater.build(inflater, this, mPartKey);
    }

    //TODO: 对齐原手工代码，这个方法签名实际上不对，应该传入ShadowActivity
    public void onChildTitleChanged(Activity childActivity, CharSequence title) {
        hostActivityDelegator.superOnChildTitleChanged(childActivity, title);
    }

    @Override
    public boolean onNavigateUpFromChild(ShadowActivity arg0) {
        throw new UnsupportedOperationException("Unsupported Yet");
    }

    @Override
    public void onChildTitleChanged(ShadowActivity arg0, CharSequence arg1) {
        throw new UnsupportedOperationException("Unsupported Yet");
    }

    public void setCallingActivity(ComponentName callingActivity) {
        mCallingActivity = callingActivity;
    }
}
