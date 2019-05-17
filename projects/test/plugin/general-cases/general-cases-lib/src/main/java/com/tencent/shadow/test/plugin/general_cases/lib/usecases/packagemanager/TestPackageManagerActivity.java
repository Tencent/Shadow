package com.tencent.shadow.test.plugin.general_cases.lib.usecases.packagemanager;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.BaseActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;

public class TestPackageManagerActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "PackageManager调用测试";
        }

        @Override
        public String getSummary() {
            return "测试PackageManager相关api的调用，确保插件调用相关api时可以正确获取到插件相关的信息";
        }

        @Override
        public Class getPageClass() {
            return TestPackageManagerActivity.class;
        }
    }

    private TextView mTvTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_packagemanager);
        mTvTextView = findViewById(R.id.text);
    }


    public void getApplicationInfo(View view){
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(),0);
            mTvTextView.setText("ApplicationInfo className:"+applicationInfo.className+
                    "\nnativeLibraryDir:"+applicationInfo.nativeLibraryDir
            +"\nmetaData:"+applicationInfo.metaData);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void getActivityInfo(View view){
        try {
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(new ComponentName(this,this.getClass()),0);
            mTvTextView.setText("activityInfo name:"+activityInfo.name
                    +"\npackageName:"+activityInfo.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void getPackageInfo(View view){
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
            mTvTextView.setText("packageInfo versionName:"+packageInfo.versionName
                    +"\nversionCode:"+packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
