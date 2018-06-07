package com.tencent.cubershi.mock_interface;

import android.app.Fragment;

import com.tencent.hydevteam.pluginframework.plugincontainer.PluginContainerActivity;

public class PluginFragment extends Fragment {
    public final MockActivity getPluginActivity() {
        final PluginContainerActivity activity = (PluginContainerActivity) getActivity();
        return (MockActivity) activity.getPluginActivity();
    }
}
