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

package com.tencent.shadow.core.common;

public final class LoggerFactory {

    volatile private static ILoggerFactory sILoggerFactory;

    public static void setILoggerFactory(ILoggerFactory loggerFactory) {
        if (sILoggerFactory != null) {
            throw new RuntimeException("不能重复初始化");
        }
        sILoggerFactory = loggerFactory;
    }

    final public static Logger getLogger(Class<?> clazz) {
        ILoggerFactory iLoggerFactory = getILoggerFactory();
        return iLoggerFactory.getLogger(clazz.getName());
    }

    public static ILoggerFactory getILoggerFactory() {
        if (sILoggerFactory == null) {
            throw new RuntimeException("没有找到 ILoggerFactory 实现，请先调用setILoggerFactory");
        }
        return sILoggerFactory;
    }
}
