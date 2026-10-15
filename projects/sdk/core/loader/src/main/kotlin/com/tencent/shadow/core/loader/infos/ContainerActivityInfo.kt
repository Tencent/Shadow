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
 */

package com.tencent.shadow.core.loader.infos

import android.content.ComponentName

/**
 * 宿主Manifest中预注册的Activity容器声明。
 *
 * [launchMode]和[taskAffinity]必须与宿主最终合并Manifest中的声明一致。
 * 多个容器可使用相同taskAffinity组成一个任务组，但componentName必须各不相同。
 * [profile]用于区分无法运行时修改的宿主Manifest能力组合，例如
 * autoRemoveFromRecents、supportsPictureInPicture和resizeableActivity。
 */
open class ContainerActivityInfo @JvmOverloads constructor(
    val componentName: ComponentName,
    val launchMode: Int,
    val taskAffinity: String,
    val profile: String = DEFAULT_PROFILE
) {
    companion object {
        const val DEFAULT_PROFILE = "default"
    }
}
