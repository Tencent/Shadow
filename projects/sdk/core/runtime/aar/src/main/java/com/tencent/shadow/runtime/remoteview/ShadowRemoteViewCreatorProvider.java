package com.tencent.shadow.runtime.remoteview;

import android.content.Context;

/**
 * Created by jaylanchen on 2018/12/7.
 */
public interface ShadowRemoteViewCreatorProvider {

    ShadowRemoteViewCreator createRemoteViewCreator(Context context);
}
