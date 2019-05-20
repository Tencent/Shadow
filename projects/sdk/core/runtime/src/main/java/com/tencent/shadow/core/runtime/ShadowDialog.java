package com.tencent.shadow.core.runtime;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

public class ShadowDialog extends Dialog {
    public ShadowDialog(Context context) {
        super(context);
    }

    public ShadowDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ShadowDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public final void setOwnerPluginActivity(ShadowActivity activity) {
        Activity hostActivity = (Activity) activity.mHostActivityDelegator.getHostActivity();
        setOwnerActivity(hostActivity);
    }

    public final ShadowActivity getOwnerPluginActivity() {
        PluginContainerActivity ownerActivity = (PluginContainerActivity) getOwnerActivity();
        if (ownerActivity != null) {
            return (ShadowActivity) ownerActivity.getPluginActivity();
        } else {
            return null;
        }
    }
}
