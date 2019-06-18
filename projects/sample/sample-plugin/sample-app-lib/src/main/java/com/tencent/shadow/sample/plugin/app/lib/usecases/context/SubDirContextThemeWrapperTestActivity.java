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

package com.tencent.shadow.sample.plugin.app.lib.usecases.context;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.tencent.shadow.sample.plugin.app.lib.gallery.BaseActivity;
import com.tencent.shadow.sample.plugin.app.lib.gallery.util.UiUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static android.os.Environment.DIRECTORY_MUSIC;
import static android.os.Environment.DIRECTORY_PODCASTS;

abstract class SubDirContextThemeWrapperTestActivity extends BaseActivity {

    private LinearLayout mRootView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(linearLayout);
        setContentView(scrollView);
        mRootView = linearLayout;
    }


    protected void fillTestValues(Context testContext) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            makeItem("getDataDir()", "TAG_GET_DATA_DIR",
                    testContext.getDataDir().getAbsolutePath()
            );
        }

        makeItem("getFilesDir()", "TAG_GET_FILES_DIR",
                testContext.getFilesDir().getAbsolutePath()
        );

        makeItem("openFileInput(\"foo\")", "TAG_OPEN_FILE_INPUT_FOO",
                getOpenFileInputAbsolutePath(testContext, "foo")
        );

        makeItem("openFileInput(\"bar\")", "TAG_OPEN_FILE_INPUT_BAR",
                getOpenFileInputAbsolutePath(testContext, "bar")
        );

        makeItem("openFileOutput(\"foo\", MODE_PRIVATE)", "TAG_OPEN_FILE_OUTPUT_FOO",
                getOpenFileOutputAbsolutePath(testContext, "foo")
        );

        makeItem("openFileOutput(\"bar\", MODE_PRIVATE)", "TAG_OPEN_FILE_OUTPUT_BAR",
                getOpenFileOutputAbsolutePath(testContext, "bar")
        );

        makeItem("deleteFile(\"foo\")", "TAG_DELETE_FILE_FOO",
                isDeleteFileSuccess(testContext, "foo")
        );

        makeItem("deleteFile(\"bar\")", "TAG_DELETE_FILE_BAR",
                isDeleteFileSuccess(testContext, "bar")
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            makeItem("getNoBackupFilesDir()", "TAG_GET_NBF_DIR",
                    testContext.getNoBackupFilesDir().getAbsolutePath()
            );
        }

        File externalMusicDir = testContext.getExternalFilesDir(DIRECTORY_MUSIC);
        makeItem("getExternalFilesDir(DIRECTORY_MUSIC)", "TAG_GET_EFD_MUSIC",
                externalMusicDir == null ? "null" : externalMusicDir.getAbsolutePath()
        );

        File externalPodcastsDir = testContext.getExternalFilesDir(DIRECTORY_PODCASTS);
        makeItem("getExternalFilesDir(DIRECTORY_MUSIC)", "TAG_GET_EFD_PODCASTS",
                externalPodcastsDir == null ? "null" : externalPodcastsDir.getAbsolutePath()
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] externalMusicDirs = testContext.getExternalFilesDirs(DIRECTORY_MUSIC);
            makeItem("getExternalFilesDirs(DIRECTORY_MUSIC)", "TAG_GET_EFDS_MUSIC",
                    Arrays.toString(externalMusicDirs)
            );

            File[] externalPodcastsDirs = testContext.getExternalFilesDirs(DIRECTORY_PODCASTS);
            makeItem("getExternalFilesDirs(DIRECTORY_MUSIC)", "TAG_GET_EFDS_PODCASTS",
                    Arrays.toString(externalPodcastsDirs)
            );
        }

        makeItem("getObbDir()", "TAG_GET_OBB_DIR",
                testContext.getObbDir().getAbsolutePath()
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            makeItem("getObbDirs()", "TAG_GET_OBB_DIRS",
                    Arrays.toString(testContext.getObbDirs())
            );
        }

        makeItem("getCacheDir()", "TAG_GET_CACHE_DIR",
                testContext.getCacheDir().getAbsolutePath()
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            makeItem("getCodeCacheDir()", "TAG_GET_CODE_CACHE_DIR",
                    testContext.getCodeCacheDir().getAbsolutePath()
            );
        }

        File externalCacheDir = testContext.getExternalCacheDir();
        makeItem("getExternalCacheDir()", "TAG_GET_EXT_CACHE_DIR",
                externalCacheDir == null ? "null" : externalCacheDir.getAbsolutePath()
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            makeItem("getExternalCacheDirs()", "TAG_GET_EXT_CACHE_DIRS",
                    Arrays.toString(testContext.getExternalCacheDirs())
            );
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            makeItem("getExternalMediaDirs()", "TAG_GET_EXT_MEDIA_DIRS",
                    Arrays.toString(testContext.getExternalMediaDirs())
            );
        }

        makeItem("getDir(\"foo\",MODE_PRIVATE)", "TAG_GET_DIR_FOO",
                testContext.getDir("foo", MODE_PRIVATE).getAbsolutePath()
        );

        makeItem("getDir(\"bar\",MODE_PRIVATE)", "TAG_GET_DIR_BAR",
                testContext.getDir("bar", MODE_PRIVATE).getAbsolutePath()
        );

        makeItem("getSharedPreferences(\"foo\",MODE_PRIVATE)", "TAG_GET_SP_FOO",
                getSharedPreferencesAbsolutePath(testContext, "foo")
        );

        makeItem("getSharedPreferences(\"bar\",MODE_PRIVATE)", "TAG_GET_SP_BAR",
                getSharedPreferencesAbsolutePath(testContext, "bar")
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            makeItem("deleteSharedPreferences(\"foo\")", "TAG_DEL_SP_FOO",
                    isDeleteSharedPreferencesSuccess(testContext, "foo")
            );

            makeItem("deleteSharedPreferences(\"bar\")", "TAG_DEL_SP_BAR",
                    isDeleteSharedPreferencesSuccess(testContext, "bar")
            );
        }

        makeItem("openOrCreateDatabase(\"foo\",MODE_PRIVATE,null)", "TAG_OOCD3_FOO",
                testContext.openOrCreateDatabase("foo", MODE_PRIVATE, null).getPath()
        );
        testContext.deleteDatabase("foo");

        makeItem("openOrCreateDatabase(\"bar\",MODE_PRIVATE,null)", "TAG_OOCD3_BAR",
                testContext.openOrCreateDatabase("bar", MODE_PRIVATE, null).getPath()
        );
        testContext.deleteDatabase("bar");

        makeItem("openOrCreateDatabase(\"foo\",MODE_PRIVATE,null,null)", "TAG_OOCD4_FOO",
                testContext.openOrCreateDatabase("foo", MODE_PRIVATE, null, null).getPath()
        );
        testContext.deleteDatabase("foo");

        makeItem("openOrCreateDatabase(\"bar\",MODE_PRIVATE,null,null)", "TAG_OOCD4_BAR",
                testContext.openOrCreateDatabase("bar", MODE_PRIVATE, null, null).getPath()
        );
        testContext.deleteDatabase("bar");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String value = "";
            try {
                testContext.moveDatabaseFrom(this, "foo");
            } catch (Exception e) {
                value = e.getMessage();
            }
            makeItem("moveDatabaseFrom(this,\"foo\")", "TAG_MOVE_DB_FROM_FOO",
                    value
            );

            try {
                testContext.moveDatabaseFrom(this, "bar");
            } catch (Exception e) {
                value = e.getMessage();
            }
            makeItem("moveDatabaseFrom(this,\"bar\")", "TAG_MOVE_DB_FROM_BAR",
                    value
            );
        }

        makeItem("deleteDatabase(\"foo_d\")", "TAG_DELETE_DB_FOO",
                isDeleteDatabaseSuccess(testContext, "foo_d")
        );

        makeItem("deleteDatabase(\"bar_d\")", "TAG_DELETE_DB_BAR",
                isDeleteDatabaseSuccess(testContext, "bar_d")
        );

        makeItem("getDatabasePath(\"foo\")", "TAG_GET_DATABASE_PATH_FOO",
                testContext.getDatabasePath("foo").getAbsolutePath()
        );

        makeItem("getDatabasePath(\"bar\")", "TAG_GET_DATABASE_PATH_BAR",
                testContext.getDatabasePath("bar").getAbsolutePath()
        );


        Context hostContext = getApplication().getBaseContext();
        hostContext.openOrCreateDatabase("foo", MODE_PRIVATE, null);
        testContext.openOrCreateDatabase("bar", MODE_PRIVATE, null);
        String[] databaseListArray = testContext.databaseList();
        List<String> databaseList = new LinkedList<>();
        Collections.addAll(databaseList, databaseListArray);
        Iterator<String> iterator = databaseList.iterator();
        String s;
        while (iterator.hasNext()) {
            s = iterator.next();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                if (s.endsWith("-wal") || s.endsWith("-shm")) {
                    iterator.remove();
                }
            } else {
                if (s.endsWith("-journal")) {
                    iterator.remove();
                }
            }
        }

        makeItem("databaseList()", "TAG_DATABASE_LIST",
                Arrays.toString(databaseList.toArray())
        );
        hostContext.deleteDatabase("foo");
        testContext.deleteDatabase("bar");
    }

    private String getOpenFileInputAbsolutePath(Context context, String name) {
        String result = "";
        try {
            context.openFileInput(name);
        } catch (FileNotFoundException e) {
            String message = e.getMessage();
            int i = message.indexOf(name);
            result = message.substring(0, i + name.length());
        }
        return result;
    }

    private String getOpenFileOutputAbsolutePath(Context context, String name) {
        File file = new File(context.getFilesDir(), name);
        if (file.exists()) {
            throw new RuntimeException("测试文件不能提前存在");
        }
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(name, MODE_PRIVATE);
            fileOutputStream.write(1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (!file.delete()) {
            throw new RuntimeException("测试文件应该被创建出来了");
        }
        return file.getAbsolutePath();
    }

    private String isDeleteFileSuccess(Context context, String name) {
        File foo = new File(context.getFilesDir(), name);
        try {
            boolean newFile = foo.createNewFile();
            if (!newFile) {
                throw new RuntimeException("没能创建新文件");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (context.deleteFile(name)) {
            return "success";
        } else {
            return "fail";
        }
    }

    private String getSharedPreferencesAbsolutePath(Context context, final String name) {
        SharedPreferences sharedPreferences
                = context.getSharedPreferences(name, MODE_PRIVATE);
        boolean commit = sharedPreferences.edit().putString("test", "test").commit();
        if (!commit) {
            throw new RuntimeException("commit failed");
        }

        Context hostContext = getApplication().getBaseContext();
        File dataDir = hostContext.getFilesDir().getParentFile();
        File sharedPrefsDir = new File(dataDir, "shared_prefs");

        File[] files = sharedPrefsDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String fileName) {
                return fileName.contains(name);
            }
        });
        if (files.length != 1) {
            throw new RuntimeException("匹配文件数量不对。");
        }
        String result = files[0].getAbsolutePath();

        if (!files[0].delete()) {
            throw new RuntimeException("删除测试文件失败");
        }

        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String isDeleteSharedPreferencesSuccess(Context context, String name) {
        File foo = new File(getSharedPreferencesAbsolutePath(context, name));
        try {
            boolean newFile = foo.createNewFile();
            if (!newFile) {
                throw new RuntimeException("没能创建新文件");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (context.deleteSharedPreferences(name)) {
            return "success";
        } else {
            return "fail";
        }
    }

    private String isDeleteDatabaseSuccess(Context context, String name) {
        File foo = context.getDatabasePath(name);
        try {
            boolean newFile = foo.createNewFile();
            if (!newFile) {
                throw new RuntimeException("没能创建新文件");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (context.deleteDatabase(name)) {
            return "success";
        } else {
            return "fail";
        }
    }

    private void makeItem(
            String labelText,
            final String viewTag,
            String value
    ) {
        ViewGroup item = UiUtil.makeItem(this, labelText, viewTag, value);
        mRootView.addView(item);
    }
}
