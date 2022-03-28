package com.leelu.shadow

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.leelu.constants.Constant
import com.leelu.shadow.manager.PluginHelper


class MainActivity : AppCompatActivity() {
    private var ll: LinearLayout? = null
    private var btnInstallPlugin:Button? = null
    private var btnInstallPlugin2:Button? = null
    private var btnloadPage:Button? = null
    private var startPluginNomal:Button? = null
    private val mHandler: Handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ll = findViewById(R.id.ll);
        btnInstallPlugin = findViewById(R.id.btn_install_plugin)
        btnInstallPlugin2 = findViewById(R.id.btn_install_plugin2)
        btnloadPage = findViewById(R.id.btn_start_plugin)
        startPluginNomal = findViewById(R.id.btn_start_plugin_nomal)


        btnInstallPlugin?.setOnClickListener {
            PluginHelper.getInstance().singlePool.execute {
                val bundle = Bundle()
                //插件 zip，这几个参数也都可以不传，直接在 PluginManager 中硬编码
                bundle.putString(
                    Constant.KEY_PLUGIN_ZIP_PATH,
                    PluginHelper.getInstance().pluginZipFile.absolutePath
                )
                MyApplication.getApp().pluginManager.enter(this,Constant.FROM_ID_INSTALL_PLUGIN,bundle,null)
            }
        }
        btnInstallPlugin2?.setOnClickListener {
            PluginHelper.getInstance().singlePool.execute {
                val bundle = Bundle()
                //插件 zip，这几个参数也都可以不传，直接在 PluginManager 中硬编码
                bundle.putString(
                    Constant.KEY_PLUGIN_ZIP_PATH,
                    PluginHelper.getInstance().pluginZipFile2.absolutePath
                )
                MyApplication.getApp().pluginManager.enter(this,Constant.FROM_ID_INSTALL_PLUGIN,bundle,null)
            }
        }

        btnloadPage?.setOnClickListener {
            PluginHelper.getInstance().singlePool.execute {
                val bundle = Bundle()
                //插件 zip，这几个参数也都可以不传，直接在 PluginManager 中硬编码
                bundle.putString(
                    Constant.KEY_PLUGIN_NAME,
                    Constant.PART_KEY
                ) // partKey 每个插件都有自己的 partKey 用来区分多个插件，如何配置在下面讲到
                bundle.putString(
                    Constant.KEY_ACTIVITY_CLASSNAME,
                    "com.leelu.plugin_app.MainActivity"
                )
                MyApplication.getApp().pluginManager.enter(this,Constant.FROM_ID_START_ACTIVITY,bundle,null)
            }
        }

        startPluginNomal?.setOnClickListener {
            PluginHelper.getInstance().singlePool.execute {
                val bundle = Bundle()
                bundle.putString(
                    Constant.KEY_ACTIVITY_CLASSNAME,
                    "com.leelu.plugin_app.MainActivity"
                )
                bundle.putString(Constant.KEY_PLUGIN_PART_KEY,"plugin_app")
                bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH,PluginHelper.getInstance().pluginZipFile.absolutePath)
                MyApplication.getApp().pluginManager.enter(this,Constant.FROM_ID_START_ACTIVITY_NORMAL,bundle,null)
            }
        }
    }

    private fun loading(view: View) {
        mHandler.post {
            ll?.removeAllViews()
            ll?.addView(view)
        }
    }

}