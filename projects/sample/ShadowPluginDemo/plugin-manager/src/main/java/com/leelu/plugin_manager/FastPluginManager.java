package com.leelu.plugin_manager;

import android.content.Context;
import android.os.RemoteException;
import android.util.Pair;

import com.tencent.shadow.core.common.Logger;
import com.tencent.shadow.core.common.LoggerFactory;
import com.tencent.shadow.core.manager.installplugin.InstalledPlugin;
import com.tencent.shadow.core.manager.installplugin.InstalledType;
import com.tencent.shadow.core.manager.installplugin.PluginConfig;
import com.tencent.shadow.dynamic.host.FailedException;
import com.tencent.shadow.dynamic.manager.PluginManagerThatUseDynamicLoader;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * CreateDate: 2022/3/17 18:00
 * Author: 李露
 * Email: lilu2@haier.com
 * Version: 1.0
 * Description:
 */
public abstract class FastPluginManager extends PluginManagerThatUseDynamicLoader {

    private static final Logger mLogger = LoggerFactory.getLogger(FastPluginManager.class);

    private ExecutorService mFixedPool = Executors.newFixedThreadPool(4);

    public FastPluginManager(Context context) {
        super(context);
    }


    public InstalledPlugin installPlugin(String zip, String hash, boolean odex) throws IOException, JSONException, InterruptedException, ExecutionException {
        final PluginConfig pluginConfig = installPluginFromZip(new File(zip), hash);
        final String uuid = pluginConfig.UUID;
        List<Future> futures = new LinkedList<>();
        List<Future<Pair<String, String>>> extractSoFutures = new LinkedList<>();
        if (pluginConfig.runTime != null && pluginConfig.pluginLoader != null) {
            Future odexRuntime = mFixedPool.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    oDexPluginLoaderOrRunTime(uuid, InstalledType.TYPE_PLUGIN_RUNTIME,
                            pluginConfig.runTime.file);
                    return null;
                }
            });
            futures.add(odexRuntime);
            Future odexLoader = mFixedPool.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    oDexPluginLoaderOrRunTime(uuid, InstalledType.TYPE_PLUGIN_LOADER,
                            pluginConfig.pluginLoader.file);
                    return null;
                }
            });
            futures.add(odexLoader);
        }
        for (Map.Entry<String, PluginConfig.PluginFileInfo> plugin : pluginConfig.plugins.entrySet()) {
            final String partKey = plugin.getKey();
            final File apkFile = plugin.getValue().file;
            Future<Pair<String, String>> extractSo = mFixedPool.submit(() -> extractSo(uuid, partKey, apkFile));
            futures.add(extractSo);
            extractSoFutures.add(extractSo);
            if (odex) {
                Future odexPlugin = mFixedPool.submit(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        oDexPlugin(uuid, partKey, apkFile);
                        return null;
                    }
                });
                futures.add(odexPlugin);
            }
        }

        for (Future future : futures) {
            future.get();
        }
        Map<String, String> soDirMap = new HashMap<>();
        for (Future<Pair<String, String>> future : extractSoFutures) {
            Pair<String, String> pair = future.get();
            soDirMap.put(pair.first, pair.second);
        }
        onInstallCompleted(pluginConfig, soDirMap);

        return getInstalledPlugins(1).get(0);
    }


    protected void callApplicationOnCreate(String partKey) throws RemoteException {
        Map map = mPluginLoader.getLoadedPlugin();
        Boolean isCall = (Boolean) map.get(partKey);
        if (isCall == null || !isCall) {
            mPluginLoader.callApplicationOnCreate(partKey);
        }
    }

    private void loadPluginLoaderAndRuntime(String uuid, String partKey) throws RemoteException, TimeoutException, FailedException {
        if (mPpsController == null) {
            bindPluginProcessService(getPluginProcessServiceName(partKey));
            waitServiceConnected(10, TimeUnit.SECONDS);
        }
        loadRunTime(uuid);
        loadPluginLoader(uuid);
    }

    protected void loadPlugin(String uuid, String partKey) throws RemoteException, TimeoutException, FailedException {
        loadPluginLoaderAndRuntime(uuid, partKey);
        Map map = mPluginLoader.getLoadedPlugin();
        if (!map.containsKey(partKey)) {
            mPluginLoader.loadPlugin(partKey);
        }
    }


    protected abstract String getPluginProcessServiceName(String partKey);

}