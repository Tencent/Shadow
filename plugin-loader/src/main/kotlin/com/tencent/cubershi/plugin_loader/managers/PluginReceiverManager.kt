package com.tencent.cubershi.plugin_loader.managers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * Created by tracyluo on 2018/6/19.
 */
open class PluginReceiverManager(private val mTotalContext: Context){
    private lateinit var mBroadcastReceiver: AllBroadcastReceiver
    /**
     * key:真action
     * val:假action
     */
    private var mApplication2TimeStamps: MutableMap<String, String> = HashMap()

    init {
        mBroadcastReceiver = AllBroadcastReceiver()
        val intentFilter = IntentFilter()
        var actionList = ArrayList<String>()
        actionList.add("testBroadCast")
        for (action in actionList){
            val timeStamps = System.nanoTime().toString()
            mApplication2TimeStamps[action] = timeStamps
            intentFilter.addAction(action)
        }
        mTotalContext.registerReceiver(mBroadcastReceiver, intentFilter)
    }
    inner class AllBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = mApplication2TimeStamps[intent?.action]?:return
            val fakeIntent = Intent()
            fakeIntent.action = action
            fakeIntent.putExtra("msg", "dynamic broadcast")
            mTotalContext.sendBroadcast(fakeIntent)
            return
        }

    }
}