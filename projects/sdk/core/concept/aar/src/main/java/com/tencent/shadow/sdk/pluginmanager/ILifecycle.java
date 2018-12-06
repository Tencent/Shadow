package com.tencent.shadow.sdk.pluginmanager;

import android.os.Bundle;

public interface ILifecycle {

    void onCreate(Bundle bundle);

    void onSaveInstanceState(Bundle outState);

    void onDestroy();

}
