package com.tencent.shadow.core.runtime;

import android.content.DialogInterface;

public interface IContainerDialogFragment extends IContainerFragment {

    void onDismiss(DialogInterface dialog);

    void superOnDismiss(DialogInterface dialog);

}
