package com.tencent.shadow.core.host.log;

public interface ILoggerFactory {

    ILogger getLogger(String name);
}

