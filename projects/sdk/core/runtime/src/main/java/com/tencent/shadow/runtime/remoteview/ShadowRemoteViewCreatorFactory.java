package com.tencent.shadow.runtime.remoteview;


import android.content.Context;

import com.tencent.shadow.runtime.ShadowContext;

/**
 * Created by jaylanchen on 2018/12/7.
 */
public class ShadowRemoteViewCreatorFactory {

    public static ShadowRemoteViewCreator createRemoteViewCreator(Context context){
        return ((ShadowContext)context).getRemoteViewCreatorProvider().createRemoteViewCreator(context);
    }
}
