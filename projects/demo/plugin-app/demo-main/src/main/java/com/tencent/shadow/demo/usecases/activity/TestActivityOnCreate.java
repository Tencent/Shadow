package com.tencent.shadow.demo.usecases.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.demo.gallery.util.ToastUtil;

public class TestActivityOnCreate extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ToastUtil.showToast(this,"onCreate");
    }

    @Override
    protected void onStart() {
        super.onStart();
        ToastUtil.showToast(this,"onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ToastUtil.showToast(this,"onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToastUtil.showToast(this,"onResume");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ToastUtil.showToast(this,"onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ToastUtil.showToast(this,"onRestoreInstanceState");
    }

    @Override
    protected void onStop() {
        super.onStop();
        ToastUtil.showToast(this,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.showToast(this,"onDestroy");
    }
}
