package com.tencent.shadow.demo.usecases.datadir;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.cases.entity.UseCase;

import java.io.File;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * 测试{@link com.tencent.shadow.runtime.ShadowContext#getDataDir()}方法逻辑
 */
public class TestDataDirActivity extends BaseActivity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "DataDir相关测试";
        }

        @Override
        public String getSummary() {
            return "测试ShadowContext#getDataDir()因BusinessName不同而隔离的相关特性";
        }

        @Override
        public Class getPageClass() {
            return TestDataDirActivity.class;
        }
    }

    private LinearLayout mRootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(makeCheckItem("getDataDir()", "GET_DATA_DIR"));

        setContentView(linearLayout);
        mRootView = linearLayout;

        fillTestValues(this);
    }

    protected void fillTestValues(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            File dataDir = context.getDataDir();
            TextView getDataDir = mRootView.findViewWithTag("GET_DATA_DIR");
            getDataDir.setText(dataDir.getAbsolutePath());
        }
    }

    private View makeCheckItem(String labelText, String viewTag) {
        TextView label = new TextView(this);
        label.setText(labelText + ":");
        label.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        TextView value = new TextView(this);
        value.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        value.setTag(viewTag);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(0, 10, 0, 10);

        linearLayout.addView(label);
        linearLayout.addView(value);

        return linearLayout;
    }
}
