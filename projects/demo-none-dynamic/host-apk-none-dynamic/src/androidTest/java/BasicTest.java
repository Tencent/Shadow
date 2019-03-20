import android.support.test.espresso.Espresso;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.tencent.shadow.demo.host.MainActivity;
import com.tencent.shadow.demo.host.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BasicTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testBasicUsage() {
        Espresso.onView(ViewMatchers.withId(R.id.startButton))
                .check(ViewAssertions.matches(ViewMatchers.withText("启动demo-plugin插件")));
    }
}
