package com.tencent.shadow.demo.gallery.cases;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.gallery.util.PluginChecker;

public class UseCaseSummaryFragment extends Fragment {

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


    public void setCase(final UseCase useCase) {
        mCaseName.setText(useCase.name);
        mCaseSummary.setText(useCase.summary);
        mStartCase.setVisibility(View.VISIBLE);

        mStartCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), useCase.pageClass);
                if (useCase.bundle != null) {
                    intent.putExtras(useCase.bundle);
                }
                startActivity(intent);
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
