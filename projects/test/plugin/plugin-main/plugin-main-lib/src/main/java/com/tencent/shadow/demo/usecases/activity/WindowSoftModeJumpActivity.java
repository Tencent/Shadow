package com.tencent.shadow.demo.usecases.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.usecases.BaseAndroidTestActivity;

public class WindowSoftModeJumpActivity extends BaseAndroidTestActivity {

    private TextView mText;

    public static String KEY_FROM_JUMP = "fromJump";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_common);
        mText = findViewById(R.id.text);
    }

    public void doClick(View view) {
        mIdlingResource.setIdleState(false);
        Intent intent = new Intent(this, TestActivityWindowSoftMode.class);
        intent.putExtra(KEY_FROM_JUMP, true);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mIdlingResource.setIdleState(true);
        String txt = data.getStringExtra("result");
        mText.setText(txt);

    }


}
