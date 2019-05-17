package com.tencent.shadow.test.plugin.general_cases.lib.usecases.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;

public class TestActivityReCreateBySystem extends Activity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "ReCreateBySystem";
        }

        @Override
        public String getSummary() {
            return "不保留活动进行测试，需要手动到开发者模式中开启";
        }

        @Override
        public Class getPageClass() {
            return TestActivityReCreateBySystem.class;
        }

        @Override
        public Bundle getPageParams() {
            Bundle bundle= new Bundle();
            bundle.putString("url", "https://www.baidu.com");
            return bundle;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_re_create_by_system);
        String url = "url : " + getIntent().getStringExtra("url");
        ((TextView) findViewById(R.id.url_tv)).setText(url);
    }
}