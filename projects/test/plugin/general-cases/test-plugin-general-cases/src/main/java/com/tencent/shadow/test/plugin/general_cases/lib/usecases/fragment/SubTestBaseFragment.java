package com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment;

import android.app.Activity;
import android.content.Context;

/**
 * 测试子类Override了onAttach的情况下TestBaseFragment是否表现正常
 */
public class SubTestBaseFragment extends TestBaseFragment {

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
