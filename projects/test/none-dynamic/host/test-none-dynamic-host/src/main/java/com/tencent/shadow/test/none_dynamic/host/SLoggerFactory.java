/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.test.none_dynamic.host;

import com.tencent.shadow.core.common.ILoggerFactory;
import com.tencent.shadow.core.common.Logger;

import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class SLoggerFactory implements ILoggerFactory {

    private ConcurrentHashMap<String, Logger> mLoggers = new ConcurrentHashMap<>();

    @Override
    public Logger getLogger(String s) {
        if (mLoggers.get(s) == null) {
            mLoggers.put(s, new SLogger(LoggerFactory.getLogger(s)));
        }
        return mLoggers.get(s);
    }


    class SLogger implements Logger {
        private org.slf4j.Logger mLogger;

        SLogger(org.slf4j.Logger logger) {
            mLogger = logger;
        }

        @Override
        public String getName() {
            return mLogger.getName();
        }

        @Override
        public boolean isTraceEnabled() {
            return mLogger.isTraceEnabled();
        }

        @Override
        public void trace(String msg) {
            mLogger.trace(msg);
        }

        @Override
        public void trace(String format, Object arg) {
            mLogger.trace(format, arg);
        }

        @Override
        public void trace(String format, Object arg1, Object arg2) {
            mLogger.trace(format, arg1, arg2);
        }

        @Override
        public void trace(String format, Object... arguments) {
            mLogger.trace(format, arguments);
        }

        @Override
        public void trace(String msg, Throwable t) {
            mLogger.trace(msg, t);
        }

        @Override
        public boolean isDebugEnabled() {
            return mLogger.isDebugEnabled();
        }

        @Override
        public void debug(String msg) {
            mLogger.debug(msg);
        }

        @Override
        public void debug(String format, Object arg) {
            mLogger.debug(format, arg);
        }

        @Override
        public void debug(String format, Object arg1, Object arg2) {
            mLogger.debug(format, arg1, arg2);
        }

        @Override
        public void debug(String format, Object... arguments) {
            mLogger.debug(format, arguments);
        }

        @Override
        public void debug(String msg, Throwable t) {
            mLogger.debug(msg, t);
        }


        @Override
        public boolean isInfoEnabled() {
            return mLogger.isInfoEnabled();
        }

        @Override
        public void info(String msg) {
            mLogger.info(msg);
        }

        @Override
        public void info(String format, Object arg) {
            mLogger.info(format, arg);
        }

        @Override
        public void info(String format, Object arg1, Object arg2) {
            mLogger.info(format, arg1, arg2);
        }

        @Override
        public void info(String format, Object... arguments) {
            mLogger.info(format, arguments);
        }

        @Override
        public void info(String msg, Throwable t) {
            mLogger.info(msg, t);
        }


        @Override
        public boolean isWarnEnabled() {
            return mLogger.isWarnEnabled();
        }

        @Override
        public void warn(String msg) {
            mLogger.warn(msg);
        }

        @Override
        public void warn(String format, Object arg) {
            mLogger.warn(format, arg);
        }

        @Override
        public void warn(String format, Object... arguments) {
            mLogger.warn(format, arguments);
        }

        @Override
        public void warn(String format, Object arg1, Object arg2) {
            mLogger.warn(format, arg1, arg2);
        }

        @Override
        public void warn(String msg, Throwable t) {
            mLogger.warn(msg, t);
        }


        @Override
        public boolean isErrorEnabled() {
            return mLogger.isErrorEnabled();
        }

        @Override
        public void error(String msg) {
            mLogger.error(msg);
        }

        @Override
        public void error(String format, Object arg) {
            mLogger.error(format, arg);
        }

        @Override
        public void error(String format, Object arg1, Object arg2) {
            mLogger.error(format, arg1, arg2);
        }

        @Override
        public void error(String format, Object... arguments) {
            mLogger.error(format, arguments);
        }

        @Override
        public void error(String msg, Throwable t) {
            mLogger.error(msg, t);
        }

    }
}
