// UuidManager.aidl
package com.tencent.shadow.dynamic.host;
// Declare any non-default types here with import statements
import com.tencent.shadow.dynamic.host.InstalledPL;

interface UuidManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    InstalledPL getInstalledPL(String uuid,int type);
}
