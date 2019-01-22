package com.tencent.shadow.demo.usecases.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.util.ToastUtil;

public class TestActivityReCreate extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recreate);
        TextView textView = findViewById(R.id.tv_msg);
        boolean isRecreate = getIntent().getBooleanExtra("reCreate", false);
        textView.setText("isRecreate:"+isRecreate);
        ToastUtil.showToast(this, "onCreate");
    }

    public void reCreate(View view) {
        getIntent().putExtra("reCreate", true);
        recreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ToastUtil.showToast(this, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ToastUtil.showToast(this, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ToastUtil.showToast(this, "onResume");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ToastUtil.showToast(this, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ToastUtil.showToast(this, "onRestoreInstanceState");
    }

    @Override
    protected void onStop() {
        super.onStop();
        ToastUtil.showToast(this, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToastUtil.showToast(this, "onDestroy");
    }
}
