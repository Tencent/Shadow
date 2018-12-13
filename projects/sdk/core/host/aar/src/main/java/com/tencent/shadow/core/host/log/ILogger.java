package com.tencent.shadow.core.host.log;

public interface ILogger {

    String getName();

    boolean isDebugEnabled();


    boolean isErrorEnabled();


    boolean isInfoEnabled();


    boolean isWarnEnabled();


    void debug(Object message);


    void debug(Object message, Throwable t);


    void info(Object message);


    void info(Object message, Throwable t);


    void warn(Object message);


    void warn(Object message, Throwable t);


    void error(Object message);


    void error(Object message, Throwable t);


}

