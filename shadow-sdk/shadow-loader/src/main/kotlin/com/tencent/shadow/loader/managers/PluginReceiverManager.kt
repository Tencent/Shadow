package com.tencent.shadow.loader.managers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * Created by tracyluo on 2018/6/19.
 */
open class PluginReceiverManager(private val mTotalContext: Context) {
    private var mBroadcastReceiver: AllBroadcastReceiver
    /**
     * key:真action
     * val:假action
     */
    private var mApplication2TimeStamps: MutableMap<String, String> = HashMap()

    /**
     * key:假action
     * val:applications
     */
    private var mAction2Applications: MutableMap<String, MutableList<String>> = HashMap()

    /**
     * key:application
     * val:List<action+className>
     */
    private var mApplication2ActionList: MutableMap<String, MutableMap<String, String>> = HashMap()

    init {
        mBroadcastReceiver = AllBroadcastReceiver()
    }

    inner class AllBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = mApplication2TimeStamps[intent?.action] ?: return
            mAction2Applications[action] ?: return
            /*
            这里如果有多个application的时候要依次判断application是否启动，没启动要启动
             */
            val fakeIntent = Intent()
            fakeIntent.action = action
            mTotalContext.sendBroadcast(fakeIntent)
            return
        }

    }

    class ReceiverInfo(val action: String, val className: String, val packageName: String)

    private fun addReceiverInfo(receiverInfo: ReceiverInfo): String? {
        var actionToAdd: String? = null
        if (mApplication2TimeStamps[receiverInfo.action] == null) {
            mApplication2TimeStamps[receiverInfo.action] = System.nanoTime().toString()
            actionToAdd = receiverInfo.action
        }
        val fakeAction = mApplication2TimeStamps[receiverInfo.action]?:""
        mAction2Applications[fakeAction]?.add(receiverInfo.packageName)
                ?: mAction2Applications.put(fakeAction, arrayListOf(receiverInfo.packageName))
        mApplication2ActionList[receiverInfo.packageName]?.put(fakeAction, receiverInfo.className)
                ?: mApplication2ActionList.put(receiverInfo.packageName, hashMapOf(fakeAction to receiverInfo.className))
        return actionToAdd
    }

    fun initReceiverInfo(infoList: List<ReceiverInfo>) {
        val intentFilter = IntentFilter()
        infoList.forEach{
            val action = addReceiverInfo(it)
            action?.let{intentFilter.addAction(action)}
        }
        mTotalContext.registerReceiver(mBroadcastReceiver, intentFilter)
    }

    fun getActionAndReceiverByApplication(application: String):MutableMap<String, String>{
        return mApplication2ActionList[application]?:HashMap()
    }
}