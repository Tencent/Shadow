package com.tencent.shadow.demo.usecases.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;


public class TestActivityWindowSoftMode extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_softmode);
    }



}
