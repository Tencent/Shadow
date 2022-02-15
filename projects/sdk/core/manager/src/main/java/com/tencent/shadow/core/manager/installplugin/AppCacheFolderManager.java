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

package com.tencent.shadow.core.manager.installplugin;

import java.io.File;

/**
 * 目录各模块的目录关系管理
 */
public class AppCacheFolderManager {

    public static File getVersionDir(File root, String appName, String version) {
        return new File(getAppDir(root, appName), version);
    }

    public static File getAppDir(File root, String appName) {
        return new File(root, appName);
    }


    public static File getODexDir(File root, String key) {
        return new File(getODexRootDir(root), key + "_odex");
    }

    public static File getODexCopiedFile(File oDexDir, String key) {
        return new File(oDexDir, key + "_oDexed");
    }


    private static File getODexRootDir(File root) {
        return new File(root, "oDex");
    }

    public static File getLibDir(File root, String key) {
        return new File(getLibRootDir(root), key + "_lib");
    }

    public static File getLibCopiedFile(File soDir, String key) {
        return new File(soDir, key + "_copied");
    }


    private static File getLibRootDir(File root) {
        return new File(root, "lib");
    }

}
