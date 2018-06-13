package com.tencent.cubershi.mock_interface;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public interface IContainerFragment {
    Fragment asFragment();

    void bindPluginFragment(MockFragment pluginFragment);

    void unbindPluginFragment();

    Activity getActivity();

    void setArguments(Bundle args);

    Bundle getArguments();
}
