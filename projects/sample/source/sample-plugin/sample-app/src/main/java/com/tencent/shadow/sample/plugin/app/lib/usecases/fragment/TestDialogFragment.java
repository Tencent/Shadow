package com.tencent.shadow.sample.plugin.app.lib.usecases.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.tencent.shadow.sample.plugin.app.lib.R;

public class TestDialogFragment extends DialogFragment {

    public static TestDialogFragment newInstance(Bundle bundle) {
        TestDialogFragment testFragment = new TestDialogFragment();
        testFragment.setArguments(bundle);
        return testFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
//        window.setWindowAnimations(android.R.style.Animation_Toast);
        window.setWindowAnimations(R.style.dialog_exit_fade_out);

        View view = inflater.inflate(R.layout.layout_fragment_test, null, false);
        TextView textView = view.findViewById(R.id.tv_msg);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String msg = bundle.getString("msg");
            if (!TextUtils.isEmpty(msg)) {
                textView.setText(msg);
            }
        }
        return view;
    }
}
