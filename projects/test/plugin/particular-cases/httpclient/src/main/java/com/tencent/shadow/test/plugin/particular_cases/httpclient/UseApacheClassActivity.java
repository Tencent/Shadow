package com.tencent.shadow.test.plugin.particular_cases.httpclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.particular_cases.httpclient.util.UiUtil;


public class UseApacheClassActivity extends Activity {

    private ViewGroup mItemViewGroup;

    org.apache.http.conn.scheme.SchemeRegistry schemeRegistry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemViewGroup = UiUtil.setActivityContentView(this);

        String exceptionName = null;
        String classLoaderName = null;
        try {
            classLoaderName = Class.forName("org.apache.http.conn.scheme.SchemeRegistry")
                    .getClassLoader().getClass().getSimpleName();
        } catch (Exception e) {
            exceptionName = e.getClass().getSimpleName();
        }

        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "exceptionName",
                        "exceptionName",
                        exceptionName
                )
        );

        mItemViewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "classLoaderName",
                        "classLoaderName",
                        classLoaderName
                )
        );
    }
}
