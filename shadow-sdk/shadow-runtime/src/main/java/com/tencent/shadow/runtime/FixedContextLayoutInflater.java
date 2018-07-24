package com.tencent.shadow.runtime;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 在HostActivityDelegate.getLayoutInflater返回的LayoutInflater虽然已经被替换为ShadowActivity作为Context了.
 * 但是Fragment在创建时还是会通过这个LayoutInflater的cloneInContext方法,传入宿主Activity作为新的Context.
 * 这里通过覆盖cloneInContext方法,避免Context被替换.
 * 见onGetLayoutInflater() of Activity$HostCallbacks in Activity.java
 *
 * @author cubershi
 */
public class FixedContextLayoutInflater extends LayoutInflater {
    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };

    public FixedContextLayoutInflater(Context context) {
        super(context);
    }

    public FixedContextLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        for (String prefix : sClassPrefixList) {
            try {
                View view = createView(name, prefix, attrs);
                if (view != null) {
                    return view;
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        return super.onCreateView(name, attrs);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return this;
    }
}
