package com.tencent.cubershi.mock_interface;

import android.app.Dialog;

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

    public Dialog getDialog() {
        return getContainerDialogFragment().getDialog();
    }
}
