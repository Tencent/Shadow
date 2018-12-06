// IProcessServicelInterface.aidl
package com.tencent.shadow.sdk.service;

interface IProcessServiceInterface {

     void loadRuntime(String uuid,String apkPath);

     IBinder loadPluginLoader(String uuid,String apkPath);

}
