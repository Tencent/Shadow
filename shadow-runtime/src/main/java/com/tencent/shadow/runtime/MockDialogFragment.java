package com.tencent.shadow.runtime;

import android.os.Bundle;

public class MockDialogFragment extends MockFragment {

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

    public MockDialog getDialog() {
        return (MockDialog) getContainerDialogFragment().getDialog();
    }

    public int getTheme() {
        return getContainerDialogFragment().getTheme();
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

    public MockDialog onCreateDialog(Bundle savedInstanceState) {
        return new MockDialog(getActivity(), getTheme());
    }
}
