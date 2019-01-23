package com.tencent.shadow.demo.usecases.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;

public class TestXmlFragmentActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment_xml_activity);

    }
}