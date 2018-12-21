// UuidManager.aidl
package com.tencent.shadow.dynamic.host;
import com.tencent.shadow.core.interface_.InstalledApk;

interface UuidManager {
    InstalledApk getPlugin(String uuid,String partKey);

    InstalledApk getPluginLoader(String uuid);

    InstalledApk getRuntime(String uuid);
}
