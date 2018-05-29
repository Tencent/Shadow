package com.tencent.cubershi.plugin_loader

import android.content.Context
import android.view.LayoutInflater

/**
 * 在HostActivityDelegate.getLayoutInflater返回的LayoutInflater虽然已经被替换为MockActivity作为Context了.
 * 但是Fragment在创建时还是会通过这个LayoutInflater的cloneInContext方法,传入宿主Activity作为新的Context.
 * 这里通过覆盖cloneInContext方法,避免Context被替换.
 * 见onGetLayoutInflater() of Activity$HostCallbacks in Activity.java
 *
 * @author cubershi
 */
class FixedContextLayoutInflater(original: LayoutInflater, newContext: Context) : LayoutInflater(original, newContext) {

    override fun cloneInContext(newContext: Context): LayoutInflater {
        return this
    }
}
