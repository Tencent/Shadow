package com.tencent.shadow.demo.basicglsurfaceview;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tencent.shadow.demo.main.R;

public class BasicGLSurfaceViewFragment extends Fragment {
    BasicGLSurfaceView mBasicGLSurfaceView;

    BasicGLSurfaceViewActivity mBasicGLSurfaceViewActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBasicGLSurfaceViewActivity = (BasicGLSurfaceViewActivity) context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mBasicGLSurfaceViewActivity = (BasicGLSurfaceViewActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mBasicGLSurfaceViewActivity = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.layout_fragment, container, false);
        mBasicGLSurfaceView = (BasicGLSurfaceView) view.getChildAt(0);
        final Button button = view.findViewById(R.id.close_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBasicGLSurfaceViewActivity != null)
                    mBasicGLSurfaceViewActivity.finish();
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mBasicGLSurfaceView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBasicGLSurfaceView.onResume();
    }
}
