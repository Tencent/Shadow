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
 * HostActivity的被委托者接口
 * <p>
 * 被委托者通过实现这个接口中声明的方法达到替代委托者实现的目的，从而将HostActivity的行为动态化。
 *
 * @author cubershi
 */
public interface HostActivityDelegate extends GeneratedHostActivityDelegate {
    void setDelegator(HostActivityDelegator delegator);

    Object getPluginActivity();

    String getLoaderVersion();
}
