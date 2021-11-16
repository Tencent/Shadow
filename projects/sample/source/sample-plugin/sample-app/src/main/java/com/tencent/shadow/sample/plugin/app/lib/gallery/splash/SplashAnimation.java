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

package com.tencent.shadow.sample.plugin.app.lib.gallery.splash;

import android.content.Context;
import android.os.Handler;

import com.tencent.shadow.sample.plugin.app.lib.gallery.util.ToastUtil;

public class SplashAnimation implements ISplashAnimation{

    private AnimationListener mAnimationListener;

    private Context mContext;

    public SplashAnimation(Context context){
        mContext = context;
    }


    @Override
    public void start() {
        ToastUtil.showToast(mContext,"animation start");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mAnimationListener != null){
                    mAnimationListener.onAnimationEnd();
                }
            }
        },2000);
    }

    @Override
    public void stop() {

    }

    @Override
    public void setAnimationListener(AnimationListener animationListener) {
        mAnimationListener = animationListener;
    }
}
