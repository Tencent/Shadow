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

package com.tencent.shadow.core.runtime;

import android.app.Activity;
import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;

public class ShadowInstrumentation extends Instrumentation {

    public void callActivityOnDestroy(ShadowActivity activity) {
        Activity hostActivity = (Activity) activity.hostActivityDelegator.getHostActivity();
        super.callActivityOnDestroy(hostActivity);
    }

    static public ShadowApplication newShadowApplication(Class<?> clazz, Context context)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        ShadowApplication app = (ShadowApplication) clazz.newInstance();

        app.attachBaseContext(context);

        //这样构造的 ShadowApplication 跟 CreateApplicationBloc 正常构造的不一样。
        //这里构造的只是个Context而已，没有插件的各种信息。
        return app;
    }


    /**
     * 因为参数签名和newActivity一样,但是返回值不一样,所以无法override
     * 只能通过transform,让newApplication转移到newShadowApplication上来
     */
    public ShadowApplication newShadowApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ShadowApplication app = (ShadowApplication) cl.loadClass(className).newInstance();
        app.attachBaseContext(context);
        return app;
    }

    /**
     * 因为参数签名和newActivity一样,但是返回值不一样,所以无法override
     * 只能通过transform,让newActivity转移到newShadowActivity上来
     */
    public ShadowActivity newShadowActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return (ShadowActivity) cl.loadClass(className).newInstance();
    }

    public void callApplicationOnCreate(ShadowApplication app) {
        app.onCreate();
    }

    /**
     * shadow启动插件activity时并不是依靠Instrumentation来操作的,所以这边的execStartActivity并没有什么实在的意义
     * 而且这个方法里面都大量的UnsupportedAppUsage方法调用,如果重写并不符合shadow零反射的原则
     * 所以,重写这个方法,但是仅仅返回一个空的ActivityResult只是是作为帮助编译通过的方法
     * <p>
     * 像com.didi.virtualapk是用自定义的Instrumentation做了一层代理,替换intent中合适的activity
     * 但是shadow的activity不是这样启动的,这个方法也不会执行,仅仅保证编译通过,能正常打出插件包,而virtualapk其实是完全失效的
     */
    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, ShadowActivity target, Intent intent, int requestCode) {
        return new ActivityResult(requestCode, intent);
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, ShadowActivity target, Intent intent, int requestCode, Bundle options) {
        return new ActivityResult(requestCode, intent);
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, Fragment target, Intent intent, int requestCode, Bundle options) {
        return new ActivityResult(requestCode, intent);
    }

    public ActivityResult execStartActivity(Context who, IBinder contextThread, IBinder token, String target, Intent intent, int requestCode, Bundle options) {
        return new ActivityResult(requestCode, intent);
    }

    /**
     * 同execStartActivity方法一样,其实插件中并不会调用到这里
     * 而且这个方法里面都大量的UnsupportedAppUsage方法调用,如果重写并不符合shadow零反射的原则
     */
    public void callActivityOnCreate(ShadowActivity activity, Bundle icicle) {
    }

    public void callActivityOnCreate(ShadowActivity activity, Bundle icicle, PersistableBundle persistentState) {
    }


}
