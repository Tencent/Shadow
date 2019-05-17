package com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.BaseActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;

public class TestXmlFragmentActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "xml中使用fragment相关测试";
        }

        @Override
        public String getSummary() {
            return "测试在Activity现实xml中定义的fragment";
        }

        @Override
        public Class getPageClass() {
            return TestXmlFragmentActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment_xml_activity);

    }
}