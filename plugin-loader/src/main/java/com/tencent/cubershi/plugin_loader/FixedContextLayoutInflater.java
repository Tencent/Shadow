package com.tencent.cubershi.plugin_loader;

import android.content.Context;
import android.view.LayoutInflater;

public class FixedContextLayoutInflater extends LayoutInflater {
    public FixedContextLayoutInflater(Context context) {
        super(context);
    }

    public FixedContextLayoutInflater(LayoutInflater original, Context newContext) {
        super(original, newContext);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return this;
    }
}
