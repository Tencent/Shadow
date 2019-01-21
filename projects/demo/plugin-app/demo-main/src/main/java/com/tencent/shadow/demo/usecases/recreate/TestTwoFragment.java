package com.tencent.shadow.demo.usecases.recreate;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TestTwoFragment extends Fragment {

    private Context context;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.e("TestTwoFragment","onCreateView");
        context = getActivity();
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView textView = new TextView(context);
        textView.setText("Good  Two");
        textView.setTextColor(Color.WHITE);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        frameLayout.addView(textView);
        frameLayout.setBackgroundColor(Color.BLACK);
        return frameLayout;
    }
}
