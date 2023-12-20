package com.tencent.shadow.test.plugin.androidx_cases.lib;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.tencent.shadow.test.plugin.androidx_cases.R;

public class FragmentContainerViewTestActivity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragmentcontainerviewtestactivity);
    }
}
