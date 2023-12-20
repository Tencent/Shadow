package com.tencent.shadow.test.plugin.androidx_cases.lib;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FooSupportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout = new LinearLayout(container.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup item = UiUtil.makeItem(getContext(),
                "msg",
                "msg",
                "FooSupportFragment");
        linearLayout.addView(item);
        return linearLayout;
    }
}
