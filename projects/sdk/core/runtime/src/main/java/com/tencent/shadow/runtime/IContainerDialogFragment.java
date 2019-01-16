package com.tencent.shadow.runtime;

import android.content.DialogInterface;

public interface IContainerDialogFragment extends IContainerFragment {

    void onDismiss(DialogInterface dialog);

    void superOnDismiss(DialogInterface dialog);

}
