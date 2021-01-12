package com.tencent.shadow.test.plugin.general_cases.app;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matchers;

/**
 * 正常安装app条件下测试general-cases-lib
 */
public abstract class NormalAppTest {
    /**
     * 检测view
     *
     * @param tag  view的tag
     * @param text view上的文字
     */
    public void matchTextWithViewTag(String tag, String text) {
        Espresso.onView(ViewMatchers.withTagValue(Matchers.<Object>is(tag)))
                .check(ViewAssertions.matches(ViewMatchers.withText(text)));
    }
}
