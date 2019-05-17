package com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.BaseActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.util.SoftKeyBoardListener;


public class TestActivityWindowSoftMode extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "windowSoftInputMode测试";
        }

        @Override
        public String getSummary() {
            return "测试插件中设置windowSoftInputMode是否生效";
        }

        @Override
        public Class getPageClass() {
            return TestActivityWindowSoftMode.class;
        }
    }

    private EditText mEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_softmode);

        mEditText = findViewById(R.id.edit_view);
        mEditText.requestFocus();

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
