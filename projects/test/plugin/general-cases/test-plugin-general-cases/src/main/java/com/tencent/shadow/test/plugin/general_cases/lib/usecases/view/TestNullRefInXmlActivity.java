package com.tencent.shadow.test.plugin.general_cases.lib.usecases.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tencent.shadow.test.plugin.general_cases.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

public class TestNullRefInXmlActivity extends Activity {

    private ViewGroup mItemViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView listView = (ListView) getLayoutInflater().inflate(R.layout.layout_test_null_ref, null);
        int cacheColorHint = listView.getCacheColorHint();

        mItemViewGroup = UiUtil.setActivityContentView(this);

        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "cacheColorHint",
                        "cacheColorHint",
                        Integer.toString(cacheColorHint, 16)
                )
        );
    }
}
