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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PluginFragmentManager {
    FragmentManager mBase;

    PluginFragmentManager(FragmentManager mBase) {
        this.mBase = mBase;
    }

    @SuppressLint("CommitTransaction")
    public PluginFragmentTransaction beginTransaction() {
        return new PluginFragmentTransaction(this, mBase.beginTransaction());
    }

    public ShadowFragment findFragmentByTag(String tag) {
        Fragment fragmentByTag = mBase.findFragmentByTag(tag);
        if (fragmentByTag instanceof IContainerFragment) {
            return ((IContainerFragment) fragmentByTag).getPluginFragment();
        } else {
            return null;
        }
    }

    public boolean executePendingTransactions() {
        return mBase.executePendingTransactions();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public List<ShadowFragment> getFragments() {
        List<Fragment> containerFragments = mBase.getFragments();
        List<ShadowFragment> pluginFragments = new ArrayList<>();
        if (containerFragments != null && containerFragments.size() > 0) {
            for (Fragment containerFragment : containerFragments) {
                if (containerFragment instanceof IContainerFragment) {
                    pluginFragments.add(((IContainerFragment) containerFragment).getPluginFragment());
                }
            }
        }
        return pluginFragments.size() > 0 ? pluginFragments : Collections.EMPTY_LIST;
    }

    public ShadowFragment getFragment(Bundle bundle, String key) {
        Fragment fragment = mBase.getFragment(bundle, key);
        return ((IContainerFragment) fragment).getPluginFragment();
    }
}
