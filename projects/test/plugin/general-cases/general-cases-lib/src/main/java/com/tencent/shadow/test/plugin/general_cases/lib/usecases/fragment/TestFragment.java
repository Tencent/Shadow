package com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;

public class TestFragment extends Fragment {

    public static TestFragment newInstance(Bundle bundle) {
        TestFragment testFragment = new TestFragment();
        testFragment.setArguments(bundle);
        return testFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_test, null, false);
        TextView textView = view.findViewById(R.id.tv_msg);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String msg = bundle.getString("msg");
            if (!TextUtils.isEmpty(msg)) {
                textView.setText(msg);
            }
        }
        return view;
    }
}
