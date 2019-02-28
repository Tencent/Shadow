package com.tencent.shadow.demo.host;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.tencent.shadow.demo.host.manager.Shadow;
import com.tencent.shadow.dynamic.host.EnterCallback;
import com.tencent.shadow.dynamic.host.PluginManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends Activity {

    /**
     * 动态加载的插件管理apk
     */
    private final static String sPluginManagerName = "dynamic-pluginmanager.apk";

    /**
     * 动态加载的插件包，里面包含以下几个部分，插件apk，插件框架apk（loader apk和runtime apk）, apk信息配置关系json文件
     */
    private final static String sPluginZip = "plugin-debug-local.zip";

    private File mPluginManagerFile;

    private File mPluginZipFile;

    private ExecutorService mSinglePool = Executors.newSingleThreadExecutor();

    private long mFromId = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.TestHostTheme);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

        mPluginManagerFile = new File(getFilesDir(), sPluginManagerName);
        mPluginZipFile = new File(getFilesDir(), sPluginZip);

        mSinglePool.execute(new Runnable() {
            @Override
            public void run() {
                preparePlugin();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        throw new RuntimeException("必须赋予权限.");
                    }
                }
            }
        }
    }

    private void preparePlugin() {
        try {
            InputStream is = getAssets().open(sPluginManagerName);
            FileUtils.copyInputStreamToFile(is, mPluginManagerFile);

            InputStream zip = getAssets().open(sPluginZip);
            FileUtils.copyInputStreamToFile(zip, mPluginZipFile);

        } catch (IOException e) {
            throw new RuntimeException("启动插件发生异常", e);
        }
    }

    private PluginManager mPluginManager;

    public void startDemoPlugin(View view) {

        mSinglePool.execute(new Runnable() {
            @Override
            public void run() {
                if (mPluginManager == null) {
                    mPluginManager = Shadow.getPluginManager(mPluginManagerFile);
                }
                Bundle bundle = new Bundle();
                bundle.putString("pluginZipPath", mPluginZipFile.getAbsolutePath());
                mPluginManager.enter(MainActivity.this, mFromId, bundle, new EnterCallback() {
                    @Override
                    public void onShowLoadingView(View view) {

                    }

                    @Override
                    public void onCloseLoadingView() {

                    }

                    @Override
                    public void onEnterComplete() {

                    }
                });
            }
        });
    }

}
