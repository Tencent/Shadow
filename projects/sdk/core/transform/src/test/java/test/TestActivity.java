package test;

import android.app.Activity;

public class TestActivity extends Activity {

    Activity foo(Activity activity) {
        activity.toString();
        return activity;
    }
}
