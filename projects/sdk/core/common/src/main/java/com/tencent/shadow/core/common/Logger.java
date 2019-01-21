package com.tencent.shadow.core.common;

public interface Logger {

    String getName();

    boolean isTraceEnabled();

    void trace(String msg);

    void trace(String format, Object arg);

    void trace(String format, Object arg1, Object arg2);

    void trace(String format, Object... arguments);

    void trace(String msg, Throwable t);

    boolean isDebugEnabled();

    void debug(String msg);

    void debug(String format, Object arg);

    void debug(String format, Object arg1, Object arg2);

    void debug(String format, Object... arguments);

    void debug(String msg, Throwable t);

    boolean isInfoEnabled();

    void info(String msg);

    void info(String format, Object arg);

    void info(String format, Object arg1, Object arg2);

    void info(String format, Object... arguments);

    void info(String msg, Throwable t);

    boolean isWarnEnabled();

    void warn(String msg);

    void warn(String format, Object arg);

    void warn(String format, Object... arguments);

    void warn(String format, Object arg1, Object arg2);

    void warn(String msg, Throwable t);

    boolean isErrorEnabled();

    void error(String msg);

    void error(String format, Object arg);

    void error(String format, Object arg1, Object arg2);

    void error(String format, Object... arguments);

    void error(String msg, Throwable t);
}

