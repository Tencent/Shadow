package com.tencent.shadow.core.runtime;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.shadow.core.runtime.container.PluginContainerActivity;

@SuppressLint("NewApi")
public class ShadowFragmentSupport {

    public static ShadowActivity fragmentGetActivity(Fragment fragment) {
        PluginContainerActivity pluginContainerActivity
                = (PluginContainerActivity) fragment.getActivity();
        // When a fragment is not attached or has already been detached, 
        // it needs to behave like Fragment.getActivity(), return null.
        if (pluginContainerActivity == null) {
            return null;
        }
        return (ShadowActivity) PluginActivity.get(pluginContainerActivity);
    }

    public static Context fragmentGetContext(Fragment fragment) {
        Context context = fragment.getContext();
        if (context instanceof PluginContainerActivity) {
            return PluginActivity.get((PluginContainerActivity) context);
        } else {
            return context;
        }
    }

    public static Object fragmentGetHost(Fragment fragment) {
        Object host = fragment.getHost();
        if (host instanceof PluginContainerActivity) {
            return PluginActivity.get((PluginContainerActivity) host);
        } else {
            return host;
        }
    }

    public static void fragmentStartActivity(Fragment fragment, Intent intent) {
        fragmentStartActivity(fragment, intent, null);
    }

    @SuppressLint("NewApi")
    public static void fragmentStartActivity(Fragment fragment, Intent intent, Bundle options) {
        ShadowContext shadowContext = fragmentGetActivity(fragment);
        Intent containerActivityIntent
                = shadowContext.mPluginComponentLauncher.convertPluginActivityIntent(intent);
        if (options == null) {
            fragment.startActivity(containerActivityIntent);
        } else {
            fragment.startActivity(containerActivityIntent, options);
        }
    }

    public static void fragmentStartActivityForResult(Fragment fragment, Intent intent, int requestCode) {
        fragmentStartActivityForResult(fragment, intent, requestCode, null);
    }

    public static void fragmentStartActivityForResult(Fragment fragment, Intent intent, int requestCode, Bundle options) {
        ShadowContext shadowContext = fragmentGetActivity(fragment);
        Intent containerActivityIntent
                = shadowContext.mPluginComponentLauncher.convertPluginActivityIntent(intent);
        if (options == null) {
            fragment.startActivityForResult(containerActivityIntent, requestCode);
        } else {
            fragment.startActivityForResult(containerActivityIntent, requestCode, options);
        }
    }

    public static Context toPluginContext(Context pluginContainerActivity) {
        return PluginActivity.get((PluginContainerActivity) pluginContainerActivity);
    }

    public static Context toOriginalContext(Context pluginActivity) {
        PluginActivity activity = (PluginActivity) pluginActivity;
        return activity.hostActivityDelegator.getHostActivity().getImplementActivity();
    }
}
