/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.runtime.container;


/**
 * ContentProviderDelegateProvider依赖注入类
 * <p>
 * dynamic-pluginloader通过这个类实现将PluginLoader中的ContentProviderDelegateProvider实现注入到plugincontainer中。
 *
 * @author owenguo
 */
public class ContentProviderDelegateProviderHolder {
    static ContentProviderDelegateProvider contentProviderDelegateProvider;


    public static void setContentProviderDelegateProvider(ContentProviderDelegateProvider contentProviderDelegateProvider) {
        ContentProviderDelegateProviderHolder.contentProviderDelegateProvider = contentProviderDelegateProvider;
        notifyDelegateProviderHolderPrepare();
    }

    private static DelegateProviderHolderPrepareListener sPrepareListener;

    public static void setDelegateProviderHolderPrepareListener(DelegateProviderHolderPrepareListener prepareListener) {
        sPrepareListener = prepareListener;
    }

    private static void notifyDelegateProviderHolderPrepare() {
        if (sPrepareListener != null) {
            sPrepareListener.onPrepare();
        }
    }

    interface DelegateProviderHolderPrepareListener {
         void onPrepare();
    }

}
