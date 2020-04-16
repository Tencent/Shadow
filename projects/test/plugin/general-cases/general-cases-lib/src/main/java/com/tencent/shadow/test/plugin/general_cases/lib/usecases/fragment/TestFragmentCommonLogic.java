package com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.shadow.test.plugin.general_cases.lib.R;

@SuppressLint("SetTextI18n")
class TestFragmentCommonLogic {

    final private Fragment fragment;

    public TestFragmentCommonLogic(Fragment fragment) {
        this.fragment = fragment;
    }

    void setTestArguments(String msg) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        if (fragment.getArguments() != null) {
            fragment.getArguments().putAll(bundle);
        } else {
            fragment.setArguments(bundle);
        }
    }

    View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.layout_fragment_test, null, false);

        addTestArgumentsView(rootView);

        addFragmentStartActivityView(rootView);
        addFragmentStartActivityWithOptionsView(rootView);

        addAttachContextView(rootView);
        addAttachActivityView(rootView);

        addInflateContextView(rootView);
        addInflateActivityView(rootView);

        addGetActivityView(rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addGetContextView(rootView);
            addGetHostView(rootView);
        }

        return rootView;
    }

    private void addTestArgumentsView(LinearLayout rootView) {
        TextView textView = rootView.findViewById(R.id.tv_msg);
        textView.setTag("TestFragmentTextView");
        Bundle bundle = fragment.getArguments();
        if (bundle != null) {
            String msg = bundle.getString("msg");
            if (!TextUtils.isEmpty(msg)) {
                textView.setText(msg);
            }
        }
    }

    private void addFragmentStartActivityView(LinearLayout rootView) {
        Button fragmentStartActivity = new Button(rootView.getContext());
        fragmentStartActivity.setText("fragmentStartActivity");
        fragmentStartActivity.setTag("fragmentStartActivity");
        fragmentStartActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentStartActivity();
            }
        });
        rootView.addView(fragmentStartActivity);
    }

    private void fragmentStartActivity() {
        Intent intent = new Intent(fragment.getActivity(), FragmentStartedActivity.class);
        fragment.startActivity(intent);
        FragmentStartedActivity.sIdlingResource.setIdleState(false);
    }

    private void addFragmentStartActivityWithOptionsView(LinearLayout rootView) {
        Button fragmentStartActivityWithOptions = new Button(rootView.getContext());
        fragmentStartActivityWithOptions.setText("fragmentStartActivityWithOptions");
        fragmentStartActivityWithOptions.setTag("fragmentStartActivityWithOptions");
        fragmentStartActivityWithOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    fragmentStartActivityWithOptions();
                }
            }
        });
        rootView.addView(fragmentStartActivityWithOptions);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fragmentStartActivityWithOptions() {
        Intent intent = new Intent(fragment.getActivity(), FragmentStartedActivity.class);
        ActivityOptions activityOptions = ActivityOptions.makeBasic();
        fragment.startActivity(intent, activityOptions.toBundle());
        FragmentStartedActivity.sIdlingResource.setIdleState(false);
    }

    private void addAttachContextView(LinearLayout rootView) {
        TextView textView = new TextView(rootView.getContext());
        textView.setTag("AttachContextView");
        if (attachContext == null) {
            textView.setText("attachContext == null");
        } else {
            textView.setText(attachContext.getClass().getCanonicalName());
        }
        rootView.addView(textView);
    }

    private void addAttachActivityView(LinearLayout rootView) {
        TextView textView = new TextView(rootView.getContext());
        textView.setTag("AttachActivityView");
        if (attachActivity == null) {
            textView.setText("attachActivity == null");
        } else {
            textView.setText(attachActivity.getClass().getCanonicalName());
        }
        rootView.addView(textView);
    }

    private Context attachContext;
    private Activity attachActivity;

    void onAttach(Context context) {
        attachContext = context;
    }

    void onAttach(Activity activity) {
        attachActivity = activity;
    }

    void onDetach() {
        attachContext = null;
        attachActivity = null;
        inflateContext = null;
        inflateActivity = null;
    }

    private void addInflateContextView(LinearLayout rootView) {
        TextView textView = new TextView(rootView.getContext());
        textView.setTag("InflateContextView");
        if (inflateContext == null) {
            textView.setText("inflateContext == null");
        } else {
            textView.setText(inflateContext.getClass().getCanonicalName());
        }
        rootView.addView(textView);
    }

    private void addInflateActivityView(LinearLayout rootView) {
        TextView textView = new TextView(rootView.getContext());
        textView.setTag("InflateActivityView");
        if (inflateActivity == null) {
            textView.setText("inflateActivity == null");
        } else {
            textView.setText(inflateActivity.getClass().getCanonicalName());
        }
        rootView.addView(textView);
    }

    private Context inflateContext;
    private Activity inflateActivity;

    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        inflateContext = context;
    }

    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        inflateActivity = activity;
    }

    private void addGetActivityView(LinearLayout rootView) {
        TextView textView = new TextView(rootView.getContext());
        textView.setTag("GetActivityView");
        Activity activity = fragment.getActivity();
        if (activity == null) {
            textView.setText("activity == null");
        } else {
            textView.setText(activity.getClass().getCanonicalName());
        }
        rootView.addView(textView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addGetContextView(LinearLayout rootView) {
        TextView textView = new TextView(rootView.getContext());
        textView.setTag("GetContextView");
        Context context = fragment.getContext();
        if (context == null) {
            textView.setText("context == null");
        } else {
            textView.setText(context.getClass().getCanonicalName());
        }
        rootView.addView(textView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void addGetHostView(LinearLayout rootView) {
        TextView textView = new TextView(rootView.getContext());
        textView.setTag("GetHostView");
        Object host = fragment.getHost();
        if (host == null) {
            textView.setText("host == null");
        } else {
            textView.setText(host.getClass().getCanonicalName());
        }
        rootView.addView(textView);
    }
}
