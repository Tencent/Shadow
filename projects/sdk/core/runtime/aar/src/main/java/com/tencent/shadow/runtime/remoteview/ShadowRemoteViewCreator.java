package com.tencent.shadow.runtime.remoteview;

import android.view.View;

/**
 * 可跨插件apk创建View的接口, 用于创建不在本插件apk的View实例
 * Created by jaylanchen on 2018/12/7.
 */
public interface ShadowRemoteViewCreator {

    /**
     * 创建View。该方法只会从
     * @param partKey 需要创建的View所在的插件标识
     * @param viewClass 需要创建的View的全限定类名
     * @return 如果创建创建成功，则会返回创建的View
     * @throws ShadowRemoteViewCreateException 如果创建失败则会抛出异常
     */
    View createView(String partKey, String viewClass) throws ShadowRemoteViewCreateException;

    /**
     * 创建View。该方法如果apkKey对应的插件不存在，则会异步去下载插件，并加载插件，最后创建View
     * @param apkKey 需要创建的View所在的插件标识
     * @param viewClassName 需要创建的View的全限定类名
     * @param callback 创建结果回调
     */
    void createView( String  apkKey, String viewClassName, ShadowRemoteViewCreateCallback callback);
}
