package com.tencent.shadow.sample.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public abstract class BaseActivity extends Activity {

    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        beforeSuperOnCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        afterSuperOnCreate(savedInstanceState);
    }

    protected void beforeSuperOnCreate(Bundle savedInstanceState) {
    }

    private void afterSuperOnCreate(Bundle savedInstanceState) {
        if (!meetPrerequisites()) {
            finishOfUnmetPrerequisite();
            return;
        }
        onCustomCreate();
        if (getLayoutResId() != 0) {
            setContentView(getLayoutResId());
        }
        rootView = getWindow().getDecorView().getRootView();
        setupView();
    }

    protected boolean meetPrerequisites() {
        return true;
    }

    protected void finishOfUnmetPrerequisite() {
        finish();
    }

    //上面的onCreate太不好用了，临时加了一个hook方法
    protected void onCustomCreate() {
    }

    protected void setupView() {
    }

    protected abstract int getLayoutResId();

    public View getRootView() {
        return rootView;
    }

    public Activity getActivity() {
        return this;
    }
}
