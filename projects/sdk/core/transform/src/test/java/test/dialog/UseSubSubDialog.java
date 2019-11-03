package test.dialog;

import com.tencent.shadow.core.runtime.ShadowActivity;

public class UseSubSubDialog {
    SubSubDialog test(SubSubDialog dialog) {
        ShadowActivity ownerActivity = dialog.getOwnerActivity();
        dialog.setOwnerActivity(ownerActivity);
        return dialog;
    }
}
