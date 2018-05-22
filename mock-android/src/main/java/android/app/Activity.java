package android.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

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
        final View inflate = LayoutInflater.from(this).inflate(layoutResID, null);
        mHostActivityDelegator.setContentView(inflate);
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
