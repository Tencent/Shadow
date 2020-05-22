package com.tencent.shadow.core.runtime;

import android.app.Activity;
import android.app.Dialog;

import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

public class ShadowDialogSupport {

    public static void dialogSetOwnerActivity(Dialog dialog, ShadowActivity activity) {
        Activity hostActivity = (Activity) activity.hostActivityDelegator.getHostActivity();
        dialog.setOwnerActivity(hostActivity);
    }

    public static ShadowActivity dialogGetOwnerActivity(Dialog dialog) {
        PluginContainerActivity ownerActivity = (PluginContainerActivity) dialog.getOwnerActivity();
        if (ownerActivity != null) {
            return (ShadowActivity) PluginActivity.get(ownerActivity);
        } else {
            return null;
        }
    }

}
