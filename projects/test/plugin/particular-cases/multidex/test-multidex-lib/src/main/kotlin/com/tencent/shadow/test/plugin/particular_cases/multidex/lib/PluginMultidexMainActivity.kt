package com.tencent.shadow.test.plugin.particular_cases.multidex.lib

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

open class PluginMultidexMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this)
        textView.setText(R.string.info)
        setContentView(textView)
    }
}