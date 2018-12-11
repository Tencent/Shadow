// IProcessServicelInterface.aidl
package com.tencent.shadow.dynamic.host;

interface IProcessServiceInterface {

     void loadRuntime(String uuid,String apkPath);

     IBinder loadPluginLoader(String uuid,String apkPath);

}
