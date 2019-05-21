package com.tencent.shadow.core.runtime.remoteview;

import android.view.View;

/**
 * 跨插件apk创建View结果回调接口
 * Created by jaylanchen on 2018/12/7.
 */
public interface ShadowRemoteViewCreateCallback {

    /**
     * view创建成功
     * @param view 创建好的View
     */
    void onViewCreateSuccess(View view);

    /**
     * view创建失败
     * @param failInfo 失败原因
     */
    void onViewCreateFailed(Exception failInfo);

}
