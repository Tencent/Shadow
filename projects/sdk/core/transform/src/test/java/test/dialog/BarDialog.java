package test.dialog;

import android.app.Dialog;

import com.tencent.shadow.core.runtime.ShadowActivity;

public class BarDialog extends Dialog {

    private void callSuper() {
        ShadowActivity ownerActivity = getOwnerActivity();
        setOwnerActivity(ownerActivity);
    }
}
