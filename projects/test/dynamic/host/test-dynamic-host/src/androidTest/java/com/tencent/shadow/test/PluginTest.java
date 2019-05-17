package com.tencent.shadow.test;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.tencent.shadow.test.dynamic.host.HostApplication;
import com.tencent.shadow.test.dynamic.host.JumpToPluginActivity;
import com.tencent.shadow.test.dynamic.host.R;
import com.tencent.shadow.test.dynamic.host.SimpleIdlingResource;
import com.tencent.shadow.test.lib.constant.Constant;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;

public abstract class PluginTest {

    /**
     * 要启动的插件intent
     * @return  插件Activity intent
     */
    abstract protected Intent getLaunchIntent();

    /**
     * 要启动的插件的PartKey
     */
    abstract protected String getPartKey();

    /**
     * 检测view
     * @param tag  view的tag
     * @param text view上的文字
     */
    public void matchTextWithViewTag(String tag,String text){
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is(tag)))
                .check(ViewAssertions.matches(ViewMatchers.withText(text)));
    }

    @Before
    public void launchActivity() {
        SimpleIdlingResource idlingResource = HostApplication.getApp().mIdlingResource;
        IdlingRegistry.getInstance().register(idlingResource);
        launchPluginActivity(getPartKey(), getLaunchIntent());

        Espresso.onView(ViewMatchers.withId(R.id.jump)).perform(ViewActions.click());
    }


    @After
    public void unregisterIdlingResource() {
        SimpleIdlingResource idlingResource = HostApplication.getApp().mIdlingResource;
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

    private static void launchPluginActivity(String partKey, Intent pluginIntent) {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), JumpToPluginActivity.class);
        intent.putExtra(Constant.KEY_PLUGIN_PART_KEY, partKey);
        intent.putExtra(Constant.KEY_ACTIVITY_CLASSNAME, pluginIntent.getComponent().getClassName());
        intent.putExtra(Constant.KEY_EXTRAS, pluginIntent.getExtras());
        ActivityScenario.launch(intent);
    }
}
