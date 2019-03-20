import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.intercepting.SingleActivityFactory;

public class PluginActivityTestRule<T extends Activity> extends ActivityTestRule<T> {
    public PluginActivityTestRule(Class<T> activityClass) {
        super(activityClass);
    }

    public PluginActivityTestRule(Class<T> activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode);
    }

    public PluginActivityTestRule(Class<T> activityClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
    }

    public PluginActivityTestRule(SingleActivityFactory<T> activityFactory, boolean initialTouchMode, boolean launchActivity) {
        super(activityFactory, initialTouchMode, launchActivity);
    }

    public PluginActivityTestRule(Class<T> activityClass, @NonNull String targetPackage, int launchFlags, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, targetPackage, launchFlags, initialTouchMode, launchActivity);
    }

    @Override
    protected Intent getActivityIntent() {
        return super.getActivityIntent();
    }
}
