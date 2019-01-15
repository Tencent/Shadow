package com.tencent.shadow.demo.main.cases;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.shadow.demo.main.R;
import com.tencent.shadow.demo.main.cases.entity.TestCase;
import com.tencent.shadow.demo.util.PluginChecker;

public class CaseSummaryFragment extends Fragment {

    private TextView mCaseName;
    private Button mStartCase;
    private TextView mCaseSummary;
    private TextView mEnvironment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_case_summary, container, false);
        bindViews(view);
        return view;
    }


    public void setCase(final TestCase testCase){
        mCaseName.setText(testCase.name);
        mCaseSummary.setText(testCase.summary);
        mStartCase.setVisibility(View.VISIBLE);

        mStartCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),testCase.pageClass));
            }
        });
    }


    private void bindViews(View view) {
        mCaseName = (TextView) view.findViewById(R.id.case_name);
        mStartCase = (Button) view.findViewById(R.id.start_case);
        mCaseSummary = (TextView) view.findViewById(R.id.case_summary);
        mEnvironment = (TextView) view.findViewById(R.id.environment);

        mEnvironment.setText(PluginChecker.isPluginMode() ? "当前环境：插件模式" : "当前环境：独立安装");
    }

}
