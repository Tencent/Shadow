// IProcessServicelInterface.aidl
package com.tencent.shadow.core.loader;

interface IProcessServiceInterface {

     void loadRuntime(String uuid,String apkPath);

     IBinder loadPluginLoader(String uuid,String apkPath);

}
