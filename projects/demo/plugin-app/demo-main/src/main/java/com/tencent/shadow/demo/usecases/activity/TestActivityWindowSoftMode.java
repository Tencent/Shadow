package com.tencent.shadow.demo.usecases.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.usecases.util.SoftKeyBoardListener;


public class TestActivityWindowSoftMode extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_softmode);

        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtra("result","hide");
                setResult(0,intent);
            }
        },3000);

        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                handler.removeCallbacksAndMessages(null);

                Intent intent = new Intent();
                intent.putExtra("result","show");
                setResult(0,intent);
            }
            @Override
            public void keyBoardHide(int height) {

            }
        });

    }

}
