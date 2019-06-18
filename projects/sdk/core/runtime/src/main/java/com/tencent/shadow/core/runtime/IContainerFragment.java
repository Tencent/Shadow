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
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public interface IContainerFragment {
    Fragment asFragment();

    ShadowFragment getPluginFragment();

    void bindPluginFragment(ShadowFragment pluginFragment);

    void unbindPluginFragment();

    Activity getActivity();

    void setArguments(Bundle args);

    Bundle getArguments();

    boolean isAdded();

    boolean isDetached();

    boolean isRemoving();

    boolean isInLayout();

    boolean isResumed();

    boolean isVisible();

    boolean isHidden();

    int getId();

    String getTag();

    View getView();

    void requestPermissions(String[] permissions, int requestCode);

    Context getContext();

    FragmentManager getChildFragmentManager();

    boolean getUserVisibleHint();

    void superSetUserVisibleHint(boolean isVisibleToUser);

    void superOnHiddenChanged(boolean hidden);

    void superSetRetainInstance(boolean retain);

    void superSetHasOptionsMenu(boolean hasMenu);

    void superSetMenuVisibility(boolean menuVisible) ;



}
