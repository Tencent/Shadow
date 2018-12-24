package com.tencent.shadow.core.common;

import android.view.View;

public interface EnterCallback {

    void onShowLoadingView(View view);

    void onCloseLoadingView();

    void onEnterComplete();
}
