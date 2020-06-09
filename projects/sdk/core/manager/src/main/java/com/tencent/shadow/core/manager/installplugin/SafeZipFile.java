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

package com.tencent.shadow.core.manager.installplugin;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 避免ZipperDown漏洞
 */
public class SafeZipFile extends ZipFile {

    public SafeZipFile(File file) throws IOException {
        super(file);
    }

    @Override
    public Enumeration<? extends ZipEntry> entries() {
        return new SafeZipEntryIterator(super.entries());
    }

    private static class SafeZipEntryIterator implements Enumeration<ZipEntry> {

        final private Enumeration<? extends ZipEntry> delegate;

        private SafeZipEntryIterator(Enumeration<? extends ZipEntry> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasMoreElements() {
            return delegate.hasMoreElements();
        }

        @Override
        public ZipEntry nextElement() {
            ZipEntry entry = delegate.nextElement();
            if (null != entry) {
                String name = entry.getName();
                if (null != name && (name.contains("../") || name.contains("..\\"))) {
                    throw new SecurityException("非法entry路径:" + entry.getName());
                }
            }
            return entry;
        }
    }
}
