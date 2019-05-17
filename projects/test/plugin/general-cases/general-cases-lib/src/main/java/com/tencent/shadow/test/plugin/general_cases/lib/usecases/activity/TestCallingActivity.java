package com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.BaseActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;

public class TestCallingActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "getCallingActivity测试";
        }

        @Override
        public String getSummary() {
            return "测试getCallingActivity API调用是否正常";
        }

        @Override
        public Class getPageClass() {
            return JumpActivity.class;
        }

        @Override
        public Bundle getPageParams() {
            Bundle bundle = new Bundle();
            bundle.putString(JumpActivity.KEY_TARGET_CLASS, "com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity.TestCallingActivity");
            return bundle;
        }
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComponentName callActivity = getCallingActivity();
        String result = callActivity == null ? "":callActivity.getClassName();
        Intent data = new Intent();
        data.putExtra("result",result);
        setResult(RESULT_OK,data);

        Toast.makeText(this,"calling:"+result,Toast.LENGTH_SHORT).show();

        finish();
    }
}
