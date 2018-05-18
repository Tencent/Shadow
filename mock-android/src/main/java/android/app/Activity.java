package android.app;

import android.os.Bundle;
import android.widget.TextView;

import com.tencent.cubershi.mock_interface.MockActivity;
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;

public class Activity extends MockActivity {
    HostActivityDelegator mHostActivityDelegator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //do nothing.
    }

    @Override
    public void setContentView(int layoutResID) {
        TextView textView = new TextView(mHostActivityDelegator.getApplicationContext());
        textView.setText("setContentView:" + layoutResID);
        mHostActivityDelegator.setContentView(textView);
    }

    @Override
    public void setContainerActivity(HostActivityDelegator delegator) {
        mHostActivityDelegator = delegator;
    }

    @Override
    public void performOnCreate(Bundle bundle) {
        onCreate(bundle);
    }
}
