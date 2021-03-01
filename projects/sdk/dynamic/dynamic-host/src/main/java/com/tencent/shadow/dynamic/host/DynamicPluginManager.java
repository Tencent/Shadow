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

package com.tencent.shadow.dynamic.host;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public final class DynamicPluginManager implements PluginManager {

    final private PluginManagerUpdater mUpdater;
    private PluginManagerImpl mManagerImpl;
    private String mCurrentImplMd5;
    private static final Logger mLogger = LoggerFactory.getLogger(DynamicPluginManager.class);

    public DynamicPluginManager(PluginManagerUpdater updater) {
        if (updater.getLatest() == null) {
            throw new IllegalArgumentException("构造DynamicPluginManager时传入的PluginManagerUpdater" +
                    "必须已经已有本地文件，即getLatest()!=null");
        }
        mUpdater = updater;
    }

    @Override
    public void enter(Context context, long fromId, Bundle bundle, EnterCallback callback) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("enter fromId:" + fromId + " callback:" + callback);
        }
        updateManagerImpl(context);
        mManagerImpl.enter(context, fromId, bundle, callback);
        mUpdater.update();
    }

    public void release() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("release");
        }
        if (mManagerImpl != null) {
            mManagerImpl.onDestroy();
            mManagerImpl = null;
        }
    }

    private void updateManagerImpl(Context context) {
        File latestManagerImplApk = mUpdater.getLatest();
        String md5 = md5File(latestManagerImplApk);
        if (mLogger.isInfoEnabled()) {
            mLogger.info("TextUtils.equals(mCurrentImplMd5, md5) : " + (TextUtils.equals(mCurrentImplMd5, md5)));
        }
        if (!TextUtils.equals(mCurrentImplMd5, md5)) {
            ManagerImplLoader implLoader = new ManagerImplLoader(context, latestManagerImplApk);
            PluginManagerImpl newImpl = implLoader.load();
            Bundle state;
            if (mManagerImpl != null) {
                state = new Bundle();
                mManagerImpl.onSaveInstanceState(state);
                mManagerImpl.onDestroy();
            } else {
                state = null;
            }
            newImpl.onCreate(state);
            mManagerImpl = newImpl;
            mCurrentImplMd5 = md5;
        }
    }

    public PluginManager getManagerImpl() {
        return mManagerImpl;
    }

    /**
     * 获取 文件的md5
     *
     * @param file 文件
     * @return md5
     */
    private static String md5File(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
        }
        return value;
    }
}
