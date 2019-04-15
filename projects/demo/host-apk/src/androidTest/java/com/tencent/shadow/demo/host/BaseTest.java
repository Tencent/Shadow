package com.tencent.shadow.demo.host;

import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;

public abstract class BaseTest {

    /**
     * 要启动的插件intent
     * @return  插件Activity intent
     */
    abstract Intent getLaunchIntent();

    /**
     * 点击跳转Activity的按钮
     */
    public void performJumpClick(){
        Espresso.onView(ViewMatchers.withId(R.id.jump)).perform(ViewActions.click());
    }

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
        PluginActivityScenario.launch(getLaunchIntent());
    }


    @After
    public void unregisterIdlingResource() {
        SimpleIdlingResource idlingResource = HostApplication.getApp().mIdlingResource;
        IdlingRegistry.getInstance().unregister(idlingResource);
    }

}
