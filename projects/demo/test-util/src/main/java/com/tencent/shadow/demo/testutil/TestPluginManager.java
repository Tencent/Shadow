package com.tencent.shadow.demo.testutil;

import android.content.Intent;

public interface TestPluginManager {
    Intent convertPluginIntent(String pluginApkPath, Intent pluginIntent);
}
