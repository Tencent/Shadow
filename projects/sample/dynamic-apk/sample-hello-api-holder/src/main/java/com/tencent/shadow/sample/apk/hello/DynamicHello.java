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

package com.tencent.shadow.sample.apk.hello;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.sample.api.hello.IHelloWorld;
import com.tencent.shadow.sample.api.hello.IHelloWorldImpl;

import java.io.File;

import static com.tencent.shadow.core.utils.Md5.md5File;


public final class DynamicHello implements IHelloWorld {

    final private HelloWorldUpdater mUpdater;
    private IHelloWorldImpl mHelloWorldImpl;
    private String mCurrentImplMd5;
    private static final Logger mLogger = LoggerFactory.getLogger(DynamicHello.class);

    public DynamicHello(HelloWorldUpdater updater) {
        if (updater.getLatest() == null) {
            throw new IllegalArgumentException("构造DynamicPluginManager时传入的PluginManagerUpdater" +
                    "必须已经已有本地文件，即getLatest()!=null");
        }
        mUpdater = updater;
    }

    @Override
    public void sayHelloWorld(Context context, TextView textView) {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("sayHelloWorld context:" + context);
        }
        updateImpl(context);
        mHelloWorldImpl.sayHelloWorld(context, textView);
        mUpdater.update();
    }

    public void release() {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("release");
        }
        if (mHelloWorldImpl != null) {
            mHelloWorldImpl.onDestroy();
            mHelloWorldImpl = null;
        }
    }

    private void updateImpl(Context context) {
        File latestImplApk = mUpdater.getLatest();
        String md5 = md5File(latestImplApk);
        if (mLogger.isInfoEnabled()) {
            mLogger.info("TextUtils.equals(mCurrentImplMd5, md5) : " + (TextUtils.equals(mCurrentImplMd5, md5)));
        }
        if (!TextUtils.equals(mCurrentImplMd5, md5)) {
            HelloImplLoader implLoader = new HelloImplLoader(context, latestImplApk);
            IHelloWorldImpl newImpl = implLoader.load();
            Bundle state;
            if (mHelloWorldImpl != null) {
                state = new Bundle();
                mHelloWorldImpl.onSaveInstanceState(state);
                mHelloWorldImpl.onDestroy();
            } else {
                state = null;
            }
            newImpl.onCreate(state);
            mHelloWorldImpl = newImpl;
            mCurrentImplMd5 = md5;
        }
    }

    public IHelloWorld getHelloWorkdImpl() {
        return mHelloWorldImpl;
    }

}
