package com.tencent.shadow.demo.usecases.packagemanager;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;

public class TestPackageManagerActivity extends BaseActivity {

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
