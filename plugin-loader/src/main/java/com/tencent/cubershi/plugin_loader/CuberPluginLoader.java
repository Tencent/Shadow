package com.tencent.cubershi.plugin_loader;

import android.app.Application;
import android.content.Context;

import com.tencent.cubershi.plugin_loader.blocs.CreateApplicationBloc;
import com.tencent.cubershi.plugin_loader.blocs.LoadApkBloc;
import com.tencent.cubershi.plugin_loader.blocs.ParsePluginApkBloc;
import com.tencent.cubershi.plugin_loader.classloaders.MockBootClassLoader;
import com.tencent.cubershi.plugin_loader.classloaders.PluginClassLoader;
import com.tencent.cubershi.plugin_loader.infos.ApkInfo;
import com.tencent.cubershi.plugin_loader.test.FakeRunningPlugin;
import com.tencent.hydevteam.common.progress.ProgressFuture;
import com.tencent.hydevteam.common.progress.ProgressFutureImpl;
import com.tencent.hydevteam.pluginframework.installedplugin.InstalledPlugin;
import com.tencent.hydevteam.pluginframework.plugincontainer.DelegateProvider;
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegate;
import com.tencent.hydevteam.pluginframework.plugincontainer.HostActivityDelegator;
import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegate;
import com.tencent.hydevteam.pluginframework.plugincontainer.HostServiceDelegator;
import com.tencent.hydevteam.pluginframework.pluginloader.LoadPluginException;
import com.tencent.hydevteam.pluginframework.pluginloader.PluginLoader;
import com.tencent.hydevteam.pluginframework.pluginloader.RunningPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CuberPluginLoader implements PluginLoader, DelegateProvider {
    private static final Logger mLogger = LoggerFactory.getLogger(CuberPluginLoader.class);

    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();

    private final File mMockAndroidFile;// TODO cubershi: 2018/5/18 这个可能要随插件版本更新.需要改到InstalledPlugin中提供.

    public CuberPluginLoader(File mockAndroidFile) {
        mMockAndroidFile = mockAndroidFile;
    }

    @Override
    public ProgressFuture<RunningPlugin> loadPlugin(final Context context, final InstalledPlugin installedPlugin) throws LoadPluginException {
        if (mLogger.isInfoEnabled()) {
            mLogger.info("loadPlugin installedPlugin=={}", installedPlugin);
        }
        if (installedPlugin.pluginFile != null && installedPlugin.pluginFile.exists()) {
            final Future<RunningPlugin> submit = mExecutorService.submit(new Callable<RunningPlugin>() {
                @Override
                public RunningPlugin call() throws Exception {
                    final ApkInfo apkInfo = ParsePluginApkBloc.parse(installedPlugin.pluginFile);
                    final ClassLoader bootClassloader = context.getClass().getClassLoader();
                    final MockBootClassLoader mockBootClassLoader = LoadApkBloc.loadMockAndroid(bootClassloader, mMockAndroidFile);
                    final PluginClassLoader pluginClassLoader = LoadApkBloc.loadPlugin(bootClassloader, installedPlugin.pluginFile, mockBootClassLoader);
                    final Application mockApplication = CreateApplicationBloc.callPluginApplicationOnCreate(pluginClassLoader, apkInfo.getApplicationClassName());
                    return new FakeRunningPlugin(mockApplication, installedPlugin);
                }
            });
            return new ProgressFutureImpl<>(submit, null);
        } else if (installedPlugin.pluginFile != null)
            throw new LoadPluginException("插件文件不存在.pluginFile==" + installedPlugin.pluginFile.getAbsolutePath());
        else throw new LoadPluginException("pluginFile==null");

    }

    @Override
    public boolean setPluginDisabled(InstalledPlugin installedPlugin) {
        return false;
    }

    @Override
    public HostActivityDelegate getHostActivityDelegate(Class<? extends HostActivityDelegator> aClass) {
        return null;
    }

    @Override
    public HostServiceDelegate getHostServiceDelegate(Class<? extends HostServiceDelegator> aClass) {
        return null;
    }
}
