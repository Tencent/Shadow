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

package com.tencent.shadow.core.runtime;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.ContextThemeWrapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * 将Context上所有get*Dir方法都放到原实现的子目录中
 */
abstract class SubDirContextThemeWrapper extends ContextThemeWrapper {
    private final Object mSync = new Object();

    /**
     * GuardedBy("mSync")
     */
    private File mDataDir, mFilesDir, mNoBackupFilesDir, mObbDir, mCacheDir, mCodeCacheDir,
            mExternalCacheDir;


    abstract String getSubDirName();

    public SubDirContextThemeWrapper() {
        super();
    }

    public SubDirContextThemeWrapper(Context base, int themeResId) {
        super(base, themeResId);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public SubDirContextThemeWrapper(Context base, Resources.Theme theme) {
        super(base, theme);
    }

    @Override
    public File getDataDir() {
        if (getSubDirName() == null) {
            return super.getDataDir();
        }
        synchronized (mSync) {
            if (mDataDir == null) {
                mDataDir = new File(super.getDataDir(), getSubDirName());
            }
            return ensurePrivateDirExists(mDataDir);
        }
    }

    @Override
    public File getFilesDir() {
        if (getSubDirName() == null) {
            return super.getFilesDir();
        }
        synchronized (mSync) {
            if (mFilesDir == null) {
                mFilesDir = new File(super.getFilesDir(), getSubDirName());
            }
            return ensurePrivateDirExists(mFilesDir);
        }
    }

    @Override
    public FileInputStream openFileInput(String name)
            throws FileNotFoundException {
        if (getSubDirName() == null) {
            return super.openFileInput(name);
        }
        File f = makeFilename(getFilesDir(), name);
        return new FileInputStream(f);
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        if (mode != MODE_PRIVATE || getSubDirName() == null) {
            return super.openFileOutput(name, mode);
        }
        final boolean append = (mode & MODE_APPEND) != 0;
        File f = makeFilename(getFilesDir(), name);
        return new FileOutputStream(f, append);
    }

    @Override
    public boolean deleteFile(String name) {
        File f = makeFilename(getFilesDir(), name);
        return f.delete();
    }

    @Override
    public File getNoBackupFilesDir() {
        if (getSubDirName() == null) {
            return super.getNoBackupFilesDir();
        }
        synchronized (mSync) {
            if (mNoBackupFilesDir == null) {
                mNoBackupFilesDir = new File(super.getNoBackupFilesDir(), getSubDirName());
            }
            return ensurePrivateDirExists(mNoBackupFilesDir);
        }
    }

    @Override
    public File getExternalFilesDir(String type) {
        if (getSubDirName() == null) {
            return super.getExternalFilesDir(type);
        }
        return ensurePrivateDirExists(new File(super.getExternalFilesDir(type), getSubDirName()));
    }

    @Override
    public File[] getExternalFilesDirs(String type) {
        if (getSubDirName() == null) {
            return super.getExternalFilesDirs(type);
        }
        File[] superResult = super.getExternalFilesDirs(type);
        File[] result = new File[superResult.length];
        for (int i = 0; i < superResult.length; i++) {
            result[i] = ensurePrivateDirExists(new File(superResult[i], getSubDirName()));
        }
        return result;
    }

    @Override
    public File getObbDir() {
        if (getSubDirName() == null) {
            return super.getObbDir();
        }
        synchronized (mSync) {
            if (mObbDir == null) {
                mObbDir = new File(super.getObbDir(), getSubDirName());
            }
            return ensurePrivateDirExists(mObbDir);
        }
    }

    @Override
    public File[] getObbDirs() {
        if (getSubDirName() == null) {
            return super.getObbDirs();
        }
        File[] superResult = super.getObbDirs();
        File[] result = new File[superResult.length];
        for (int i = 0; i < superResult.length; i++) {
            result[i] = ensurePrivateDirExists(new File(superResult[i], getSubDirName()));
        }
        return result;
    }

    @Override
    public File getCacheDir() {
        if (getSubDirName() == null) {
            return super.getCacheDir();
        }
        synchronized (mSync) {
            if (mCacheDir == null) {
                mCacheDir = new File(super.getCacheDir(), getSubDirName());
            }
            return ensurePrivateDirExists(mCacheDir);
        }
    }

    @Override
    public File getCodeCacheDir() {
        if (getSubDirName() == null) {
            return super.getCodeCacheDir();
        }
        synchronized (mSync) {
            if (mCodeCacheDir == null) {
                mCodeCacheDir = new File(super.getCodeCacheDir(), getSubDirName());
            }
            return ensurePrivateDirExists(mCodeCacheDir);
        }
    }

    @Override
    public File getExternalCacheDir() {
        if (getSubDirName() == null) {
            return super.getExternalCacheDir();
        }
        synchronized (mSync) {
            if (mExternalCacheDir == null) {
                mExternalCacheDir = new File(super.getExternalCacheDir(), getSubDirName());
            }
            return ensurePrivateDirExists(mExternalCacheDir);
        }
    }

    @Override
    public File[] getExternalCacheDirs() {
        if (getSubDirName() == null) {
            return super.getExternalCacheDirs();
        }
        File[] superResult = super.getExternalCacheDirs();
        File[] result = new File[superResult.length];
        for (int i = 0; i < superResult.length; i++) {
            result[i] = ensurePrivateDirExists(new File(superResult[i], getSubDirName()));
        }
        return result;
    }

    @Override
    public File[] getExternalMediaDirs() {
        if (getSubDirName() == null) {
            return super.getExternalMediaDirs();
        }
        File[] superResult = super.getExternalMediaDirs();
        File[] result = new File[superResult.length];
        for (int i = 0; i < superResult.length; i++) {
            result[i] = ensurePrivateDirExists(new File(superResult[i], getSubDirName()));
        }
        return result;
    }

    @Override
    public File getDir(String name, int mode) {
        if (mode != MODE_PRIVATE || getSubDirName() == null) {
            return super.getDir(name, mode);
        }
        return ensurePrivateDirExists(new File(super.getDir(name, mode), getSubDirName()));
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (mode != MODE_PRIVATE || getSubDirName() == null) {
            return super.getSharedPreferences(name, mode);
        } else {
            return super.getSharedPreferences(makeSubName(name), mode);
        }
    }

    @Override
    public boolean deleteSharedPreferences(String name) {
        if (getSubDirName() == null) {
            return super.deleteSharedPreferences(name);
        } else {
            return super.deleteSharedPreferences(makeSubName(name));
        }
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        if (mode != MODE_PRIVATE || getSubDirName() == null) {
            return super.openOrCreateDatabase(name, mode, factory);
        } else {
            return super.openOrCreateDatabase(makeSubName(name), mode, factory);
        }
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        if (mode != MODE_PRIVATE || getSubDirName() == null) {
            return super.openOrCreateDatabase(name, mode, factory, errorHandler);
        } else {
            return super.openOrCreateDatabase(makeSubName(name), mode, factory, errorHandler);
        }
    }

    @Override
    public boolean moveDatabaseFrom(Context sourceContext, String name) {
        if (getSubDirName() == null) {
            return super.moveDatabaseFrom(sourceContext, name);
        } else {
            throw new UnsupportedOperationException("暂不支持");
        }
    }

    @Override
    public boolean deleteDatabase(String name) {
        if (getSubDirName() == null) {
            return super.deleteDatabase(name);
        } else {
            return super.deleteDatabase(makeSubName(name));
        }
    }

    @Override
    public File getDatabasePath(String name) {
        if (getSubDirName() == null) {
            return super.getDatabasePath(name);
        } else {
            return super.getDatabasePath(makeSubName(name));
        }
    }

    @Override
    public String[] databaseList() {
        if (getSubDirName() == null) {
            return super.databaseList();
        } else {
            String[] databaseList = super.databaseList();
            boolean[] record = new boolean[databaseList.length];
            int size = 0;
            for (int i = 0; i < databaseList.length; i++) {
                if (databaseList[i].startsWith(getSubDirName())) {
                    record[i] = true;
                    size++;
                } else {
                    record[i] = false;
                }
            }
            String[] result = new String[size];
            int j = 0;
            for (int i = 0; i < record.length; i++) {
                if (record[i]) {
                    result[j++] = databaseList[i];
                }
            }
            return result;
        }
    }

    private String makeSubName(String name) {
        return getSubDirName() + "_" + name;
    }

    private static File ensurePrivateDirExists(File dir) {
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();
        return dir;
    }

    private static File makeFilename(File base, String name) {
        if (name.indexOf(File.separatorChar) < 0) {
            return new File(base, name);
        }
        throw new IllegalArgumentException(
                "File " + name + " contains a path separator");
    }
}
