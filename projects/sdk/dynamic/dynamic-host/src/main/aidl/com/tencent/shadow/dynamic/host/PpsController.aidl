// PpsController.aidl
package com.tencent.shadow.dynamic.host;

//PluginProcessServiceController
interface PpsController {

     void loadRuntime(String uuid,String apkPath);

     IBinder loadPluginLoader(String uuid,String apkPath);

}
