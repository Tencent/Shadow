package com.tencent.shadow.test.cases.plugin_main;

import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * 广播测试
 */
public class PluginBroadcastReceiverTest extends PluginMainAppTest {

    private final static String INTENT_NORMAL_ACTION = "com.tencent.test.normal.action";
    private final static String INTENT_DYNAMIC_ACTION = "com.tencent.test.action.DYNAMIC";

    private final static String MSG_NORMAL = "收到测试静态广播发送";
    private final static String MSG_DYNAMIC = "收到动态动态广播发送";


    @Override
    protected Intent getLaunchIntent() {
        Intent pluginIntent = new Intent();
        String packageName = ApplicationProvider.getApplicationContext().getPackageName();
        pluginIntent.setClassName(
                packageName,
                "com.tencent.shadow.test.plugin.general_cases.lib.usecases.receiver.TestReceiverActivity"
        );
        return pluginIntent;
    }

    /**
     * 动态广播测试
     */
    @Test
    public void testDynamicBroadcastReceiver(){
        //测试动态广播可以正常收到，并且action，extra，以及context都是正确的
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("button_dynamic"))).
                perform(ViewActions.click());
        matchTextWithViewTag("text",
                String.format("action:%s msg:%s isShadowContext:%s",INTENT_DYNAMIC_ACTION,
                        MSG_DYNAMIC,"true"));

        //测试动态广播反注册后就收不到广播了
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("button_unRegister_dynamic"))).
                perform(ViewActions.click());
        matchTextWithViewTag("text", "");

    }
    /**
     * 静态广播测试
     */
    @Test
    public void testStaticBroadcastReceiver(){
        //测试静态广播可以正常收到，并且action，extra，以及context都是正确的
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is("button_static"))).
                perform(ViewActions.click());

        matchTextWithViewTag("text",
                String.format("action:%s msg:%s isShadowContext:%s",INTENT_NORMAL_ACTION,
                        MSG_NORMAL,"true"));
    }
}
