package com.tencent.shadow.runtime;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

public interface IContainerFragment {
    Fragment asFragment();

    ShadowFragment getPluginFragment();

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

    void requestPermissions(String[] permissions, int requestCode);

    Context getContext();

    FragmentManager getChildFragmentManager();

    boolean getUserVisibleHint();

    void superSetUserVisibleHint(boolean isVisibleToUser);

    void superOnHiddenChanged(boolean hidden);

    void superSetRetainInstance(boolean retain);

    void superSetHasOptionsMenu(boolean hasMenu);

    void superSetMenuVisibility(boolean menuVisible) ;



}
