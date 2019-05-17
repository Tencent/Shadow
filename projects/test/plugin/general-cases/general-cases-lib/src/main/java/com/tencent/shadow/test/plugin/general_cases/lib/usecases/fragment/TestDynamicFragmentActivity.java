package com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.test.plugin.general_cases.lib.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.BaseActivity;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;

public class TestDynamicFragmentActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "代码添加fragment相关测试";
        }

        @Override
        public String getSummary() {
            return "测试通过代码添加一个fragment";
        }

        @Override
        public Class getPageClass() {
            return TestDynamicFragmentActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment_activity);

        String msg = "这是一个动态添加的fragment";
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        TestFragment testFragment = TestFragment.newInstance(bundle);
        getFragmentManager().beginTransaction().add(R.id.fragment_container,testFragment).commit();
    }
}
