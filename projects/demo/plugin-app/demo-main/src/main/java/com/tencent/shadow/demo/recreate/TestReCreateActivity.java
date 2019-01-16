package com.tencent.shadow.demo.recreate;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tencent.shadow.demo.main.R;

public class TestReCreateActivity extends Activity {

    private int type = 0;

    private Fragment mFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_recreate);
        type = getIntent().getIntExtra("type",0);
        init();
    }

    public void init() {
        if(type == 0){
            TestOneFragment fragment = new TestOneFragment();
            mFragment = fragment;
            getFragmentManager().beginTransaction().add(R.id.fragement,fragment,"F1").commitAllowingStateLoss();
        }else{
            TestTwoFragment fragment = new TestTwoFragment();
            mFragment = fragment;
            getFragmentManager().beginTransaction().add(R.id.fragement,fragment,"F2").commitAllowingStateLoss();
        }
    }

    public void reCreate(View view) {
        getFragmentManager().beginTransaction().remove(mFragment).commitAllowingStateLoss();
        if(type == 0){
            getIntent().putExtra("type",1);
        }else {
            getIntent().putExtra("type",0);
        }
        recreate();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
