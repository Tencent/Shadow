package com.tencent.shadow.demo.usecases.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;

public class TestDynamicFragmentActivity extends BaseActivity {

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
