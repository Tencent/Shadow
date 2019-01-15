/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.shadow.demo.basicglsurfaceview;

import android.app.Activity;
import android.app.Application;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import com.tencent.shadow.demo.main.R;
import com.tencent.shadow.demo.recreate.TestReCreateActivity;
import com.tencent.shadow.demo.webview.WebActivity;


public class BasicGLSurfaceViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.layout);
        final MyApplication application = (MyApplication) getApplication();
        final String mInit = application.mInit;
        System.out.println(mInit.length());

        BasicGLSurfaceViewFragment fragment = new BasicGLSurfaceViewFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frameLayout, fragment, "F2");
        fragmentTransaction.commitAllowingStateLoss();

        testApplicationCallback();

    }

    public void startAnotherActivity(View view) {
        Intent intent = new Intent(this, TestSoLoadActivity.class);
        intent.putExtra("TEST", "加载SO");

        intent.putExtra("TestParcelable", new TestParcelable());

        startActivityForResult(intent, 999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999) {
            if (resultCode != 888)
                throw new RuntimeException();
            else
                Toast.makeText(this, "收到999请求的888结果", Toast.LENGTH_SHORT).show();
        }
    }

    public void startServiceInternal(View view){
        Intent intent = new Intent(this, MyService1.class);
        getApplicationContext().startService(intent);
    }

    public void startServiceTestActivity(View view) {
        Intent intent = new Intent(this, ServiceTestActivity.class);
        startActivity(intent);
    }

    public void startTestReceiver(View view) {
        Intent intent = new Intent(this, TestReceiver.class);
        startActivity(intent);
    }

    private void testApplicationCallback() {
        getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (activity instanceof ServiceTestActivity) {
                    Toast.makeText(BasicGLSurfaceViewActivity.this, "ServiceTestActivity onCreate", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    public void startWebActivity(View view){
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this.getPackageName(), "com.tencent.intervideo.sixgodcontainer.proxyactivitys.PluginDefaultProxyActivity"));
        startActivity(intent);
    }

    public void showNotification(View view){
        Notification.Builder nb = new Notification.Builder(this);
        nb.setDefaults(Notification.DEFAULT_LIGHTS);
        nb.setContentText("QQ直播正在运行");
        nb.setOngoing(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            nb.setVisibility(Notification.VISIBILITY_PUBLIC);
        }
        nb.setPriority(Notification.PRIORITY_HIGH);
        nb.setSmallIcon(android.R.drawable.sym_def_app_icon);

        Intent intent = new Intent(this, WebActivity.class);


        PendingIntent pendingIntent = PendingIntent.getActivity(this
                , (int) SystemClock.uptimeMillis()
                , intent
                , PendingIntent.FLAG_UPDATE_CURRENT);

        nb.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(hashCode(), nb.build());
    }


    public void reCreate(View view){
        startActivity(new Intent(this, TestReCreateActivity.class));
    }
}
