package com.tencent.shadow.demo.usecases.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.util.ToastUtil;


public class TestActivityOrientation extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_orientation);
        ToastUtil.showToast(this,"onCreate");
    }


    public void setOrientation(View view){
       int orientation =  getRequestedOrientation();
       if(orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
       }else {
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
       }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
