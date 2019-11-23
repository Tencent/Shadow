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
 * 宿主容器委托提供者
 * <p>
 * 负责提供宿主容器委托实现
 *
 * @author cubershi
 */
public interface DelegateProvider {
    String LOADER_VERSION_KEY = "LOADER_VERSION";

    String PROCESS_ID_KEY = "PROCESS_ID_KEY";

    /**
     * 获取与delegator相应的HostActivityDelegate
     *
     * @param delegator HostActivity委托者
     * @return HostActivity被委托者
     */
    HostActivityDelegate getHostActivityDelegate(Class<? extends HostActivityDelegator> delegator);

}

