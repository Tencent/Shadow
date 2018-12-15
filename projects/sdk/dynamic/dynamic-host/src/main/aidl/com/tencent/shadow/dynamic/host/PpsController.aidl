// PpsController.aidl
package com.tencent.shadow.dynamic.host;

import com.tencent.shadow.dynamic.host.UuidManager;
//PpsController
interface PpsController {

     void loadRuntime(String uuid);

     IBinder loadPluginLoader(String uuid);

     void setUuidManager(UuidManager uuidManager);

}
