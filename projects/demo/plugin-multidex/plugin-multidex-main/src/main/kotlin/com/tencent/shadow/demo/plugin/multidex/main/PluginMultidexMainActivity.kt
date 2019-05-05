package com.tencent.shadow.demo.plugin.multidex.main

import android.app.Activity
import android.os.Bundle
import android.widget.TextView

class PluginMultidexMainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this)
        textView.setText(R.string.info)
        setContentView(textView)
    }
}