package com.tencent.shadow.core.interface_;

import android.os.Bundle;

public interface ILifecycle {

    void onCreate(Bundle bundle);

    void onSaveInstanceState(Bundle outState);

    void onDestroy();

}
