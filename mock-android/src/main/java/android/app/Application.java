package android.app;

import android.content.Context;
import android.util.Log;

import com.tencent.cubershi.mock_interface.MockApplication;


public class Application implements MockApplication {
    Context mHostAppContext;

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
}
