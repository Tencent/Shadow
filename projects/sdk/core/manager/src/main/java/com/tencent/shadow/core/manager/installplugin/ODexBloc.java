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
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexClassLoader;

public class ODexBloc {

    private static ConcurrentHashMap<String, Object> sLocks = new ConcurrentHashMap<>();

    public static void oDexPlugin(File apkFile, File oDexDir, File copiedTagFile) throws InstallPluginException {

        String key = apkFile.getAbsolutePath();
        Object lock = sLocks.get(key);
        if (lock == null) {
            lock = new Object();
            sLocks.put(key, lock);
        }


        synchronized (lock) {
            if (copiedTagFile.exists()) {
                return;
            }

            //如果odex目录存在但是个文件，不是目录，那超出预料了。删除了也不一定能工作正常。
            if (oDexDir.exists() && oDexDir.isFile()) {
                throw new InstallPluginException("oDexDir=" + oDexDir.getAbsolutePath() + "已存在，但它是个文件，不敢贸然删除");
            }
            //创建oDex目录
            oDexDir.mkdirs();

            new DexClassLoader(apkFile.getAbsolutePath(), oDexDir.getAbsolutePath(), null, ODexBloc.class.getClassLoader());

            try {
                copiedTagFile.createNewFile();
            } catch (IOException e) {
                throw new InstallPluginException("oDexPlugin完毕 创建tag文件失败：" + copiedTagFile.getAbsolutePath(), e);
            }
        }
    }
}
