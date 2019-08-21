package com.tencent.shadow.test.cases.plugin_main.application_info;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.test.core.app.ApplicationProvider;

import static android.content.pm.PackageManager.GET_META_DATA;

/**
 * 在插件中通过PackageManager.getApplicationInfo()获取其他正常安装应用的ApplicationInfo测试。
 *
 * @author shifujun
 */
public class PackageManagerGetOtherInstalledApplicationInfoTest extends CommonApplicationInfoTest {

    @Override
    protected String getTag() {
        return "PackageManagerGetOtherInstalled";
    }

    private ApplicationInfo getApplicationInfoFromHost() throws PackageManager.NameNotFoundException {
        PackageManager packageManager = ApplicationProvider.getApplicationContext().getPackageManager();
        return packageManager.getApplicationInfo("com.android.shell", GET_META_DATA);
    }

    @Override
    public void testSourceDir() throws Exception {
        ApplicationInfo applicationInfoFromHost = getApplicationInfoFromHost();
        matchTextWithViewTag("TAG_sourceDir_" + getTag(), applicationInfoFromHost.sourceDir);
    }

    @Override
    public void testNativeLibraryDir() throws Exception {
        ApplicationInfo applicationInfoFromHost = getApplicationInfoFromHost();
        matchTextWithViewTag("TAG_nativeLibraryDir_" + getTag(), applicationInfoFromHost.nativeLibraryDir);
    }

    @Override
    public void testMetaData() {
        matchTextWithViewTag("TAG_metaData_" + getTag(), "");
    }

    @Override
    public void testClassName() throws Exception {
        ApplicationInfo applicationInfoFromHost = getApplicationInfoFromHost();
        String className = applicationInfoFromHost.className;
        if (className == null) {
            className = "";
        }
        matchTextWithViewTag("TAG_className_" + getTag(),
                className);
    }
}
