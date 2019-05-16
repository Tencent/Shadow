package com.tencent.shadow.demo.usecases.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.cases.entity.UseCase;

public class TestDialogActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "Dialog 相关测试";
        }

        @Override
        public String getSummary() {
            return "测试show Dialog";
        }

        @Override
        public Class getPageClass() {
            return TestDialogActivity.class;
        }
    }



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
