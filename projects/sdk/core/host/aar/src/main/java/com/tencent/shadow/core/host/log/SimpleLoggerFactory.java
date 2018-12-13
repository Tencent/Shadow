package com.tencent.shadow.core.host.log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleLoggerFactory implements ILoggerFactory {

    ConcurrentMap<String, ILogger> loggerMap;

    public SimpleLoggerFactory() {
        loggerMap = new ConcurrentHashMap<String, ILogger>();
    }

    public ILogger getLogger(String name) {
        ILogger simpleLogger = loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        } else {
            ILogger newInstance = new LogcatLogger(name);
            ILogger oldInstance = loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }


    void reset() {
        loggerMap.clear();
    }
}
