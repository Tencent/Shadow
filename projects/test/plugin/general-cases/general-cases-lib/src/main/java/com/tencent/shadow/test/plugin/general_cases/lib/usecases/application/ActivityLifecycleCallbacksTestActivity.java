package com.tencent.shadow.test.plugin.general_cases.lib.usecases.application;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.TestApplication;
import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

import java.util.List;

/**
 * 在
 * com.tencent.shadow.test.plugin.general_cases.lib.gallery.TestApplication
 * 中注册一个ActivityLifecycleCallbacks，专门监听
 * com.tencent.shadow.test.plugin.general_cases.lib.usecases.application.TestApplicationActivity
 * 的生命周期。
 * 然后用这个Activity打印出监听记录进行测试。
 */
public class ActivityLifecycleCallbacksTestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup viewGroup = UiUtil.setActivityContentView(this);

        List<String> record = TestApplication.getInstance().getTestActivityLifecycleCallbacksRecord();


        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "ActivityCreateTest",
                        "ActivityCreateTest",
                        record.toString()
                )
        );

        Button recreateButton = new Button(this);
        recreateButton.setText("recreate");
        recreateButton.setTag("recreate");
        recreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLifecycleCallbacksTestActivity.this.recreate();
            }
        });
        viewGroup.addView(recreateButton);
    }

}
