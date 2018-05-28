package com.tencent.cubershi.plugin_loader

import android.content.Context
import android.view.LayoutInflater

class FixedContextLayoutInflater(original: LayoutInflater, newContext: Context) : LayoutInflater(original, newContext) {

    override fun cloneInContext(newContext: Context): LayoutInflater {
        return this
    }
}
