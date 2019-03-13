package com.tencent.shadow.demo.host;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.shadow.demo.host.manager.Shadow;
import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.host.PluginManager;


public class PluginLoadActivity extends Activity {

    private PluginManager mPluginManager;

    private long mFromId = 1001;

    private ViewGroup mViewGroup;

    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        mViewGroup = findViewById(R.id.container);

        startDemoPlugin();
    }


    public void startDemoPlugin() {

        PluginHelper.getInstance().singlePool.execute(new Runnable() {
            @Override
            public void run() {
                if (mPluginManager == null) {
                    mPluginManager = Shadow.getPluginManager(PluginHelper.getInstance().pluginManagerFile);
                }

                Bundle bundle = new Bundle();
                bundle.putString("pluginZipPath", PluginHelper.getInstance().pluginZipFile.getAbsolutePath());

                mPluginManager.enter(PluginLoadActivity.this, mFromId, bundle, new EnterCallback() {
                    @Override
                    public void onShowLoadingView(final View view) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mViewGroup.addView(view);
                            }
                        });
                    }

                    @Override
                    public void onCloseLoadingView() {
                        finish();
                    }

                    @Override
                    public void onEnterComplete() {

                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewGroup.removeAllViews();
    }
}
