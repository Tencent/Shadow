package com.tencent.shadow.test.plugin.androidx_cases.lib;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

/**
 * LiveData以Activity为LifecycleOwner
 */
public class LiveDataWithActivityTestActivity extends ComponentActivity {

    final private MutableLiveData<String> mLiveData = new MutableLiveData<>();
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String tag = "data";
        ViewGroup viewGroup = UiUtil.setActivityContentView(this);
        ViewGroup item = UiUtil.makeItem(this, "Data", tag, "");
        viewGroup.addView(item);
        mTextView = item.findViewWithTag(tag);

        final Observer<String> observer = new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String data) {
                mTextView.setText(data);
            }
        };

        mLiveData.observe(this, observer);

        Button button = new Button(this);
        button.setText("ChangeLiveData");
        button.setTag("button");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLiveData.setValue("onClick");
            }
        });
        viewGroup.addView(button);
    }
}
