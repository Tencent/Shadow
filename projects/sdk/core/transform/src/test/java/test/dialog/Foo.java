package test.dialog;

import android.app.Dialog;

import com.tencent.shadow.core.runtime.ShadowActivity;

public class Foo {
    Dialog foo(Dialog dialog) {
        ShadowActivity ownerActivity = dialog.getOwnerActivity();
        dialog.setOwnerActivity(ownerActivity);
        return dialog;
    }

    BarDialog bar(BarDialog dialog) {
        ShadowActivity ownerActivity = dialog.getOwnerActivity();
        dialog.setOwnerActivity(ownerActivity);
        return dialog;
    }
}
