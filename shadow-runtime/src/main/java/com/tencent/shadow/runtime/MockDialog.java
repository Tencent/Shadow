package com.tencent.shadow.runtime;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.tencent.shadow.container.PluginContainerActivity;

public class MockDialog extends Dialog {
    public MockDialog(Context context) {
        super(context);
    }

    public MockDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MockDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public final void setOwnerPluginActivity(MockActivity activity) {
        Activity hostActivity = (Activity) activity.mHostActivityDelegator.getHostActivity();
        setOwnerActivity(hostActivity);
    }

    public final MockActivity getOwnerPluginActivity() {
        PluginContainerActivity ownerActivity = (PluginContainerActivity) getOwnerActivity();
        if (ownerActivity != null) {
            return (MockActivity) ownerActivity.getPluginActivity();
        } else {
            return null;
        }
    }
}
