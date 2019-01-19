package com.tencent.shadow.runtime.container;


/**
 * ContentProvider宿主容器委托提供者
 * <p>
 * 负责提供宿主容器委托实现
 *
 * @author owenguo
 */
public interface ContentProviderDelegateProvider {

    /**
     * 获取与delegator相应的HostContentProviderDelegator
     *
     * @param delegator HostContentProvider 委托者
     * @return HostContentProvider被委托者
     */
    HostContentProviderDelegate  getHostContentProviderDelegate(Class<? extends HostContentProviderDelegator> delegator);
}
