package com.tencent.shadow.runtime;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
        return super.openFileInput(getSubDirName() + name);
    }

    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        if (getSubDirName() == null) {
            return super.openFileOutput(name, mode);
        }
        return super.openFileOutput(getSubDirName() + name, mode);
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
        if (getSubDirName() == null) {
            return super.getDir(name, mode);
        }
        return ensurePrivateDirExists(new File(super.getDir(name, mode), getSubDirName()));
    }

    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        if (mode != MODE_PRIVATE || getSubDirName() == null) {
            return super.getSharedPreferences(name, mode);
        } else {
            return super.getSharedPreferences("ShadowPlugin_" + getSubDirName() + "_" + name, mode);
        }
    }

    @Override
    public boolean deleteSharedPreferences(String name) {
        if (getSubDirName() == null) {
            return super.deleteSharedPreferences(name);
        } else {
            return super.deleteSharedPreferences("ShadowPlugin_" + getSubDirName() + "_" + name);
        }
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
