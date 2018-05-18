package android.app;

import android.util.Log;

import com.tencent.cubershi.mock_interface.MockApplication;


public class Application implements MockApplication {
    public void onCreate() {
        Log.i("MockApplication", "MockApplication test");
    }
}
