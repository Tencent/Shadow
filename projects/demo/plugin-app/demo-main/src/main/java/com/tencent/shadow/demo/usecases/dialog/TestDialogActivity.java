package com.tencent.shadow.demo.usecases.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;

public class TestDialogActivity extends BaseActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_activity);
    }

    public void show(View view){
        TestDialog dialog = new TestDialog(this);
        dialog.setContentView(R.layout.layout_dialog);

        dialog.show();
    }
}
