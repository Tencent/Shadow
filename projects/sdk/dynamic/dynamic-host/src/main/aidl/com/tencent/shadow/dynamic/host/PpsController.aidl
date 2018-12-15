// PpsController.aidl
package com.tencent.shadow.dynamic.host;

import com.tencent.shadow.dynamic.host.InstalledPLCallback;
//PpsController
interface PpsController {

     void loadRuntime(String uuid);

     IBinder loadPluginLoader(String uuid);

     void setInstalledPLCallback(InstalledPLCallback callback);

}
