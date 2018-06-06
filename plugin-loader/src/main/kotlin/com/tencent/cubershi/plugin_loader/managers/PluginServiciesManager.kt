package com.tencent.cubershi.plugin_loader.managers


import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import com.tencent.cubershi.mock_interface.MockService.PluginServiceManager

/**
 * Created by tracyluo on 2018/6/6.
 */
abstract class PluginServiciesManager : PluginServiceManager{

    override fun startService(intent: Intent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stopService(name: Intent?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun bindService(service: Intent?, conn: ServiceConnection?, flags: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unbindService(conn: ServiceConnection?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    abstract fun onBindContainerActivity(mockService: ComponentName): ComponentName
}