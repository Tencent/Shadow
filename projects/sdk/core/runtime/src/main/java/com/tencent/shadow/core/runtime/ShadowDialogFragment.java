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

import android.content.DialogInterface;
import android.os.Bundle;

public class ShadowDialogFragment extends ShadowFragment {

    private ContainerDialogFragment getContainerDialogFragment() {
        return (ContainerDialogFragment) mContainerFragment;
    }

    public void setStyle(int style, int theme) {
        if (mIsAppCreateFragment) {
            getContainerDialogFragment().setStyle(style, theme);
        }
    }

    public void setCancelable(boolean cancelable) {
        if (mIsAppCreateFragment) {
            getContainerDialogFragment().setCancelable(cancelable);
        }
    }

    public ShadowDialog getDialog() {
        return (ShadowDialog) getContainerDialogFragment().getDialog();
    }

    public int getTheme() {
        return getContainerDialogFragment().getTheme();
    }

    public boolean getShowsDialog() {
        return getContainerDialogFragment().getShowsDialog();
    }

    public void setShowsDialog(boolean showsDialog) {
        getContainerDialogFragment().setShowsDialog(showsDialog);
    }

    public void dismiss() {
        getContainerDialogFragment().dismiss();
    }

    public void dismissAllowingStateLoss() {
        getContainerDialogFragment().dismissAllowingStateLoss();
    }

    public void show(PluginFragmentManager manager, String tag) {
        getContainerDialogFragment().show(manager.mBase, tag);
    }

    public ShadowDialog onCreateDialog(Bundle savedInstanceState) {
        return new ShadowDialog(getActivity(), getTheme());
    }

    public void onDismiss(DialogInterface dialog) {
        getContainerDialogFragment().superOnDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
    }
}
