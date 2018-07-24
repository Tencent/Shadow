package com.tencent.shadow.runtime;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

public interface IContainerFragment {
    Fragment asFragment();

    void bindPluginFragment(ShadowFragment pluginFragment);

    void unbindPluginFragment();

    Activity getActivity();

    void setArguments(Bundle args);

    Bundle getArguments();

    boolean isAdded();

    boolean isDetached();

    boolean isRemoving();

    boolean isInLayout();

    boolean isResumed();

    boolean isVisible();

    boolean isHidden();

    int getId();

    String getTag();

    View getView();
}
