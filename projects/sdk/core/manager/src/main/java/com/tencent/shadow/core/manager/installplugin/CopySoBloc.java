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


import android.text.TextUtils;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class CopySoBloc {

    private static final Logger mLogger = LoggerFactory.getLogger(CopySoBloc.class);

    private static ConcurrentHashMap<String, Object> sLocks = new ConcurrentHashMap<>();

    public static void copySo(File apkFile, File soDir, File copiedTagFile, String filter) throws InstallPluginException {
        String key = apkFile.getAbsolutePath();
        Object lock = sLocks.get(key);
        if (lock == null) {
            lock = new Object();
            sLocks.put(key, lock);
        }

        synchronized (lock) {

            if (TextUtils.isEmpty(filter) || copiedTagFile.exists()) {
                return;
            }

            //如果so目录存在但是个文件，不是目录，那超出预料了。删除了也不一定能工作正常。
            if (soDir.exists() && soDir.isFile()) {
                throw new InstallPluginException("soDir=" + soDir.getAbsolutePath() + "已存在，但它是个文件，不敢贸然删除");
            }

            //创建so目录
            soDir.mkdirs();

            ZipFile zipFile = null;
            try {
                zipFile = new SafeZipFile(apkFile);
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().startsWith(filter)) {
                        String fileName = entry.getName().substring(filter.length());
                        MinFileUtils.writeOutZipEntry(zipFile, entry, soDir, fileName);
                    }
                }

                // 外边创建完成标记
                try {
                    copiedTagFile.createNewFile();
                } catch (IOException e) {
                    throw new InstallPluginException("创建so复制完毕 创建tag文件失败：" + copiedTagFile.getAbsolutePath(), e);
                }

            } catch (Exception e) {
                throw new InstallPluginException("解压so 失败 apkFile:" + apkFile.getAbsolutePath() + " abi:" + filter, e);
            } finally {
                try {
                    if (zipFile != null) {
                        zipFile.close();
                    }
                } catch (IOException e) {
                    mLogger.warn("zip关闭时出错忽略", e);
                }
            }
        }
    }


}

