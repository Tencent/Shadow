package com.tencent.shadow.core.host.log;

import android.util.Log;

public class LogcatLogger implements ILogger {

    private String name;

    public LogcatLogger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public void debug(Object message) {
        Log.d(name, message.toString());
    }

    @Override
    public void debug(Object message, Throwable t) {
        Log.d(name, message.toString(), t);
    }

    @Override
    public void info(Object message) {
        Log.i(name, message.toString());
    }

    @Override
    public void info(Object message, Throwable t) {
        Log.i(name, message.toString(), t);
    }

    @Override
    public void warn(Object message) {
        Log.w(name, message.toString());
    }

    @Override
    public void warn(Object message, Throwable t) {
        Log.w(name, message.toString(), t);
    }

    @Override
    public void error(Object message) {
        Log.e(name, message.toString());
    }

    @Override
    public void error(Object message, Throwable t) {
        Log.e(name, message.toString(), t);
    }
}
