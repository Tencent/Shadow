package com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

public class TargetFragmentTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup viewGroup = UiUtil.setActivityContentView(this);

        Fragment fragA = new TestNormalFragment();
        Fragment fragB = new TestNormalFragment();
        getFragmentManager().beginTransaction()
                .add(fragA, "fragA")
                .add(fragB, "fragB")
                .commit();

        fragA.setTargetFragment(fragB, 47);

        String tagOfTarget = fragA.getTargetFragment().getTag();
        int targetRequestCode = fragA.getTargetRequestCode();

        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "tagOfTarget",
                        "tagOfTarget",
                        String.valueOf(tagOfTarget)
                )
        );

        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "targetRequestCode",
                        "targetRequestCode",
                        String.valueOf(targetRequestCode)
                )
        );
    }
}
