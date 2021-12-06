package com.tencent.shadow.sample.host.plugin_view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.shadow.sample.host.lib.HostAddPluginViewContainer;
import com.tencent.shadow.sample.host.lib.HostAddPluginViewContainerHolder;

public class HostAddPluginViewActivity extends Activity implements HostAddPluginViewContainer {
    private ViewGroup mPluginViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout activityContentView = new LinearLayout(this);
        activityContentView.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams wrapContent = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        TextView note = new TextView(this);
        note.setLayoutParams(wrapContent);
        note.setText("需要先启动插件sample-plugin-app后，才能点下面的加载插件View");

        Button loadButton = new Button(this);
        loadButton.setText("加载插件View");
        loadButton.setOnClickListener(this::loadPluginView);
        loadButton.setLayoutParams(wrapContent);

        ViewGroup pluginViewContainer = new LinearLayout(this);
        pluginViewContainer.setLayoutParams(wrapContent);
        mPluginViewContainer = pluginViewContainer;

        View[] views = {
                note,
                loadButton,
                pluginViewContainer
        };
        for (View view : views) {
            activityContentView.addView(view);
        }
        setContentView(activityContentView);
    }

    private void loadPluginView(View view) {
        //简化逻辑，只允许点一次
        view.setEnabled(false);

        //因为当前Activity和插件都在:plugin进程，不能直接操作主进程的manager对象，所以通过一个广播调用manager。
        Intent intent = new Intent();
        intent.setPackage(getPackageName());
        intent.setAction("sample_host.manager.startPluginService");

        final int id = System.identityHashCode(this);
        HostAddPluginViewContainerHolder.instances.put(id, this);
        intent.putExtra("id", id);

        sendBroadcast(intent);
    }

    @Override
    public void addView(View view) {
        mPluginViewContainer.addView(view);
    }
}
