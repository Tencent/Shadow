package com.tencent.shadow.demo.host;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.tencent.shadow.demo.testutil.Constant;

final class PluginActivityScenario {
    static ActivityScenario<JumpToPluginActivity> launch(Intent pluginIntent) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), JumpToPluginActivity.class);
        intent.putExtra(Constant.KEY_ACTIVITY_CLASSNAME, pluginIntent.getComponent().getClassName());
        intent.putExtra(Constant.KEY_EXTRAS, pluginIntent.getExtras());
        return ActivityScenario.launch(intent);
    }
}
