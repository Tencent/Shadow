package test.dialog;

import android.app.Dialog;

import com.tencent.shadow.core.runtime.ShadowActivity;

public class UseDialog {
    Dialog test(Dialog dialog) {
        ShadowActivity ownerActivity = dialog.getOwnerActivity();
        dialog.setOwnerActivity(ownerActivity);
        return dialog;
    }
}
