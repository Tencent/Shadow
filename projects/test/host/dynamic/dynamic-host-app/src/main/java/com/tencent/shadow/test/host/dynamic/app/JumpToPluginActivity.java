package com.tencent.shadow.test.host.dynamic.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.tencent.shadow.demo.testutil.Constant;
import com.tencent.shadow.dynamic.host.EnterCallback;

public class JumpToPluginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump_to_plugin);
    }

    public void jump(View view) {
        HostApplication.getApp().loadPluginManager(PluginHelper.getInstance().pluginManagerFile);

        Bundle bundle = new Bundle();
        bundle.putString(Constant.KEY_PLUGIN_ZIP_PATH, PluginHelper.getInstance().pluginZipFile.getAbsolutePath());
        bundle.putString(Constant.KEY_PLUGIN_PART_KEY, getIntent().getStringExtra(Constant.KEY_PLUGIN_PART_KEY));
        bundle.putString(Constant.KEY_ACTIVITY_CLASSNAME, getIntent().getStringExtra(Constant.KEY_ACTIVITY_CLASSNAME));
        bundle.putBundle(Constant.KEY_EXTRAS, getIntent().getBundleExtra(Constant.KEY_EXTRAS));

        final SimpleIdlingResource idlingResource = HostApplication.getApp().mIdlingResource;
        idlingResource.setIdleState(false);
        HostApplication.getApp().getPluginManager()
                .enter(this, Constant.FROM_ID_START_ACTIVITY, bundle, new EnterCallback() {
                    @Override
                    public void onShowLoadingView(View view) {

                    }

                    @Override
                    public void onCloseLoadingView() {
                        idlingResource.setIdleState(true);
                    }

                    @Override
                    public void onEnterComplete() {

                    }
                });

    }
}
