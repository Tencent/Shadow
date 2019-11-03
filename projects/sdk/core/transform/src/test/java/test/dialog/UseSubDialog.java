package test.dialog;

import com.tencent.shadow.core.runtime.ShadowActivity;

public class UseSubDialog {
    SubDialog test(SubDialog dialog) {
        ShadowActivity ownerActivity = dialog.getOwnerActivity();
        dialog.setOwnerActivity(ownerActivity);
        return dialog;
    }
}
