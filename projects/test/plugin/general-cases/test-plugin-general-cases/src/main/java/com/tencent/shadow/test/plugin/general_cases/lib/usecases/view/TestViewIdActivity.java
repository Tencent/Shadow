package com.tencent.shadow.test.plugin.general_cases.lib.usecases.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.R;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

public class TestViewIdActivity extends Activity {

    private ViewGroup mItemViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View inflateView = getLayoutInflater().inflate(R.layout.layout_test_view_id, null);
        int idInXml = inflateView.getId();
        int idInJava = R.id.test_id;
        int idInResources = getResources().getIdentifier("test_id", "id", getPackageName());

        mItemViewGroup = UiUtil.setActivityContentView(this);

        boolean isSame = idInXml == idInJava && idInXml == idInResources;

        addItem("idInXml", idInXml);
        addItem("idInJava", idInJava);
        addItem("idInResources", idInResources);
        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "isSame",
                        "isSame",
                        Boolean.toString(isSame)
                )
        );
    }

    private void addItem(String key, int value) {
        ViewGroup item = UiUtil.makeItem(
                this,
                key,
                key,
                Integer.toHexString(value)
        );
        mItemViewGroup.addView(item);
    }
}
