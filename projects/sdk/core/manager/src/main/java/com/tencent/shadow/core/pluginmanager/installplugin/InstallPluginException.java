package com.tencent.shadow.core.pluginmanager.installplugin;

public class InstallPluginException extends Exception {

    public InstallPluginException(String message) {
        super(message);
    }

    public InstallPluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
