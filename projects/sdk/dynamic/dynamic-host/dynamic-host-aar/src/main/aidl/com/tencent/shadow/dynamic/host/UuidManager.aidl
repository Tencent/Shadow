// UuidManager.aidl
package com.tencent.shadow.dynamic.host;
// Declare any non-default types here with import statements
import com.tencent.shadow.dynamic.host.InstalledPart;

interface UuidManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    InstalledPart getInstalledPL(String uuid,int type);

    InstalledPart getInstalledPlugin(String uuid,String partKey);
}
