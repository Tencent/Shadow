package android.app;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.tencent.cubershi.mock_interface.MockApplication;


public class Application implements MockApplication {
    Context mHostAppContext;
    Resources mPluginResources;

    public void onCreate() {
        Log.i("MockApplication", "MockApplication test");
    }

    @Override
    public Context getHostApplicationContext() {
        return mHostAppContext;
    }

    @Override
    public void setHostApplicationContext(Context hostAppContext) {
        mHostAppContext = hostAppContext;
    }

    @Override
    public void setPluginResources(Resources resources) {
        mPluginResources = resources;
    }
}
