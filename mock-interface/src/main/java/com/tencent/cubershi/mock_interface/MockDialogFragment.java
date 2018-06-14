package com.tencent.cubershi.mock_interface;

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
}
