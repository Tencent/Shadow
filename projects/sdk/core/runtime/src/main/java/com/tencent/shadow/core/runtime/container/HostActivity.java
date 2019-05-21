package com.tencent.shadow.core.runtime.container;

import android.app.Activity;
import android.view.Window;

/**
 * 表示一个Activity是宿主程序中的Activity
 *
 * @author cubershi
 */
public interface HostActivity {
    /**
     * 返回Activity对象本身
     *
     * @return Activity对象本身
     */
    Activity getImplementActivity();

    /**
     * 返回Activity的Window
     *
     * @return Activity的Window
     */
    Window getImplementWindow();
}
