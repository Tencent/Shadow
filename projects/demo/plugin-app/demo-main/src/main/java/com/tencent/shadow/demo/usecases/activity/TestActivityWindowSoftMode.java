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

        //是否来自单元测试的中转Activity
        final boolean isFromJump = getIntent().getBooleanExtra(WindowSoftModeJumpActivity.KEY_FROM_JUMP, false);

        final Handler handler = new Handler();

        if (isFromJump) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setResult("hide");
                }
            }, 3000);
        }


        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                if (isFromJump) {
                    handler.removeCallbacksAndMessages(null);

                    setResult("show");
                }
            }

            @Override
            public void keyBoardHide(int height) {

            }
        });

    }

    private void setResult(String result){
        Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(0, intent);
        finish();
    }

}
