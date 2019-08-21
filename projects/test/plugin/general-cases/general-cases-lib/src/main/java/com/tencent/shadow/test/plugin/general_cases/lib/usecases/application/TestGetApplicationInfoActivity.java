package com.tencent.shadow.test.plugin.general_cases.lib.usecases.application;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;

import static android.content.pm.PackageManager.GET_META_DATA;

public class TestGetApplicationInfoActivity extends Activity {
    private ViewGroup mItemViewGroup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItemViewGroup = UiUtil.setActivityContentView(this);

        parseApplicationInfo("Context", this.getApplicationInfo());
        try {
            parseApplicationInfo("PackageManagerGetSelf",
                    getPackageManager().getApplicationInfo(getPackageName(), GET_META_DATA));
            parseApplicationInfo("PackageManagerGetOtherInstalled",
                    getPackageManager().getApplicationInfo("com.android.shell", GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseApplicationInfo(String tag, ApplicationInfo info) {
        String sourceDir = info.sourceDir;
        String nativeLibraryDir = info.nativeLibraryDir;
        Bundle metaData = info.metaData;
        String className = info.className;

        makeItem(tag + ":sourceDir", "TAG_sourceDir_" + tag,
                sourceDir
        );
        makeItem(tag + ":nativeLibraryDir", "TAG_nativeLibraryDir_" + tag,
                nativeLibraryDir
        );
        makeItem(tag + ":metaData", "TAG_metaData_" + tag,
                metaData != null ? metaData.getString("test_meta") : null
        );
        makeItem(tag + ":className", "TAG_className_" + tag,
                className
        );
    }

    private void makeItem(
            String labelText,
            final String viewTag,
            String value
    ) {
        ViewGroup item = UiUtil.makeItem(this, labelText, viewTag, value);
        mItemViewGroup.addView(item);
    }
}
