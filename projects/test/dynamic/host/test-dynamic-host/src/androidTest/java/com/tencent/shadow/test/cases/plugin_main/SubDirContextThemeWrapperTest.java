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

package com.tencent.shadow.test.cases.plugin_main;

import static android.content.Context.MODE_PRIVATE;
import static android.os.Environment.DIRECTORY_MUSIC;
import static android.os.Environment.DIRECTORY_PODCASTS;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

abstract class SubDirContextThemeWrapperTest extends PluginMainAppTest {

    private static final String PREFIX = "ShadowPlugin";
    private static final String BUSINESS_NAME = "general-cases";
    private static final String EXPECT_NAME = PREFIX + "_" + BUSINESS_NAME;

    @Test
    public void testGetDataDir() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            String hostDataDir = ApplicationProvider.getApplicationContext().getDataDir().getAbsolutePath();
            assertTextAndFileExist("TAG_GET_DATA_DIR", hostDataDir + "/" + EXPECT_NAME);
        }
    }

    @Test
    public void testGetFilesDir() {
        String hostFilesDir = ApplicationProvider.getApplicationContext().getFilesDir().getAbsolutePath();
        assertTextAndFileExist("TAG_GET_FILES_DIR", hostFilesDir + "/" + EXPECT_NAME);
    }

    @Test
    public void testOpenFileInput() {
        String hostFilesDir = ApplicationProvider.getApplicationContext().getFilesDir().getAbsolutePath();
        matchTextWithViewTag(
                "TAG_OPEN_FILE_INPUT_FOO",
                hostFilesDir + "/" + EXPECT_NAME + "/" + "foo"
        );
        matchTextWithViewTag(
                "TAG_OPEN_FILE_INPUT_BAR",
                hostFilesDir + "/" + EXPECT_NAME + "/" + "bar"
        );
    }

    @Test
    public void testOpenFileOutput() {
        String hostFilesDir = ApplicationProvider.getApplicationContext().getFilesDir().getAbsolutePath();
        matchTextWithViewTag(
                "TAG_OPEN_FILE_OUTPUT_FOO",
                hostFilesDir + "/" + EXPECT_NAME + "/" + "foo"
        );
        matchTextWithViewTag(
                "TAG_OPEN_FILE_OUTPUT_BAR",
                hostFilesDir + "/" + EXPECT_NAME + "/" + "bar"
        );
    }

    @Test
    public void testDeleteFile() {
        matchTextWithViewTag(
                "TAG_DELETE_FILE_FOO",
                "success"
        );
        matchTextWithViewTag(
                "TAG_DELETE_FILE_BAR",
                "success"
        );
    }

    @Test
    public void testGetNoBackupFilesDir() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String hostDir = ApplicationProvider.getApplicationContext().getNoBackupFilesDir().getAbsolutePath();
            assertTextAndFileExist("TAG_GET_NBF_DIR", hostDir + "/" + EXPECT_NAME);
        }
    }

    @Test
    public void testGetExternalFilesDir() {
        String hostDir = ApplicationProvider.getApplicationContext().getExternalFilesDir(DIRECTORY_MUSIC).getAbsolutePath();
        assertTextAndFileExist("TAG_GET_EFD_MUSIC", hostDir + "/" + EXPECT_NAME);
        hostDir = ApplicationProvider.getApplicationContext().getExternalFilesDir(DIRECTORY_PODCASTS).getAbsolutePath();
        assertTextAndFileExist("TAG_GET_EFD_PODCASTS", hostDir + "/" + EXPECT_NAME);
    }

    @Test
    public void testGetExternalFilesDirs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] hostDirs = ApplicationProvider.getApplicationContext().getExternalFilesDirs(DIRECTORY_MUSIC);
            File[] expectsDirs = new File[hostDirs.length];
            for (int i = 0; i < hostDirs.length; i++) {
                File hostDir = hostDirs[i];
                File expectDir = new File(hostDir, EXPECT_NAME);
                expectsDirs[i] = expectDir;
            }
            matchTextWithViewTag(
                    "TAG_GET_EFDS_MUSIC",
                    Arrays.toString(expectsDirs)
            );

            hostDirs = ApplicationProvider.getApplicationContext().getExternalFilesDirs(DIRECTORY_PODCASTS);
            expectsDirs = new File[hostDirs.length];
            for (int i = 0; i < hostDirs.length; i++) {
                File hostDir = hostDirs[i];
                File expectDir = new File(hostDir, EXPECT_NAME);
                expectsDirs[i] = expectDir;
            }
            matchTextWithViewTag(
                    "TAG_GET_EFDS_PODCASTS",
                    Arrays.toString(expectsDirs)
            );
        }
    }

    @Test
    public void testGetObbDir() {
        String hostDir = ApplicationProvider.getApplicationContext().getObbDir().getAbsolutePath();
        assertTextAndFileExist("TAG_GET_OBB_DIR", hostDir + "/" + EXPECT_NAME);
    }

    @Test
    public void testGetObbDirs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] hostDirs = ApplicationProvider.getApplicationContext().getObbDirs();
            File[] expectsDirs = new File[hostDirs.length];
            for (int i = 0; i < hostDirs.length; i++) {
                File hostDir = hostDirs[i];
                File expectDir = new File(hostDir, EXPECT_NAME);
                expectsDirs[i] = expectDir;
            }
            matchTextWithViewTag(
                    "TAG_GET_OBB_DIRS",
                    Arrays.toString(expectsDirs)
            );
        }
    }

    @Test
    public void testGetCacheDir() {
        String hostDir = ApplicationProvider.getApplicationContext().getCacheDir().getAbsolutePath();
        assertTextAndFileExist("TAG_GET_CACHE_DIR", hostDir + "/" + EXPECT_NAME);
    }

    @Test
    public void testGetCodeCacheDir() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String hostDir = ApplicationProvider.getApplicationContext().getCodeCacheDir().getAbsolutePath();
            assertTextAndFileExist("TAG_GET_CODE_CACHE_DIR", hostDir + "/" + EXPECT_NAME);
        }
    }

    @Test
    public void testGetExternalCacheDir() {
        String hostDir = ApplicationProvider.getApplicationContext().getExternalCacheDir().getAbsolutePath();
        assertTextAndFileExist("TAG_GET_EXT_CACHE_DIR", hostDir + "/" + EXPECT_NAME);
    }

    @Test
    public void testGetExternalCacheDirs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] hostDirs = ApplicationProvider.getApplicationContext().getExternalCacheDirs();
            File[] expectsDirs = new File[hostDirs.length];
            for (int i = 0; i < hostDirs.length; i++) {
                File hostDir = hostDirs[i];
                File expectDir = new File(hostDir, EXPECT_NAME);
                expectsDirs[i] = expectDir;
            }
            matchTextWithViewTag(
                    "TAG_GET_EXT_CACHE_DIRS",
                    Arrays.toString(expectsDirs)
            );
        }
    }

    @Test
    public void testGetExternalMediaDirs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            File[] hostDirs = ApplicationProvider.getApplicationContext().getExternalMediaDirs();
            File[] expectsDirs = new File[hostDirs.length];
            for (int i = 0; i < hostDirs.length; i++) {
                File hostDir = hostDirs[i];
                File expectDir = new File(hostDir, EXPECT_NAME);
                expectsDirs[i] = expectDir;
            }
            matchTextWithViewTag(
                    "TAG_GET_EXT_MEDIA_DIRS",
                    Arrays.toString(expectsDirs)
            );
        }
    }

    @Test
    public void testGetDir() {
        String hostDir = ApplicationProvider.getApplicationContext().getDir("foo", MODE_PRIVATE).getAbsolutePath();
        assertTextAndFileExist("TAG_GET_DIR_FOO", hostDir + "/" + EXPECT_NAME);

        hostDir = ApplicationProvider.getApplicationContext().getDir("bar", MODE_PRIVATE).getAbsolutePath();
        assertTextAndFileExist("TAG_GET_DIR_BAR", hostDir + "/" + EXPECT_NAME);
    }

    @Test
    public void testGetSharedPreferences() {
        final String testSharedPreferences = "testGetSharedPreferences";
        SharedPreferences sharedPreferences
                = ApplicationProvider.getApplicationContext().getSharedPreferences(testSharedPreferences, MODE_PRIVATE);
        boolean commit = sharedPreferences.edit().putString("test", "test").commit();
        if (!commit) {
            throw new RuntimeException("commit failed");
        }

        File sharedPrefs = new File(ApplicationProvider.getApplicationContext().getFilesDir().getParent(), "shared_prefs");
        File[] files = sharedPrefs.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.contains(testSharedPreferences)) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (files.length != 1) {
            throw new RuntimeException("匹配文件数量不对。");
        }
        String hostSPFilePath = files[0].getParentFile().getAbsolutePath();

        matchTextWithViewTag("TAG_GET_SP_FOO", hostSPFilePath + "/" + EXPECT_NAME + "_foo.xml");
        matchTextWithViewTag("TAG_GET_SP_BAR", hostSPFilePath + "/" + EXPECT_NAME + "_bar.xml");
    }

    @Test
    public void testDeleteSharedPreferences() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            matchTextWithViewTag(
                    "TAG_DEL_SP_FOO",
                    "success"
            );
            matchTextWithViewTag(
                    "TAG_DEL_SP_BAR",
                    "success"
            );
        }
    }

    @Test
    public void testOpenOrCreateDatabase() {
        String hostDatabasePath = ApplicationProvider.getApplicationContext().getDatabasePath("test").getParentFile().getAbsolutePath();
        matchTextWithViewTag(
                "TAG_OOCD3_FOO",
                hostDatabasePath + "/" + EXPECT_NAME + "_foo"
        );
        matchTextWithViewTag(
                "TAG_OOCD3_BAR",
                hostDatabasePath + "/" + EXPECT_NAME + "_bar"
        );
        matchTextWithViewTag(
                "TAG_OOCD4_FOO",
                hostDatabasePath + "/" + EXPECT_NAME + "_foo"
        );
        matchTextWithViewTag(
                "TAG_OOCD4_BAR",
                hostDatabasePath + "/" + EXPECT_NAME + "_bar"
        );
    }

    @Test
    public void testMoveDatabaseFrom() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            matchTextWithViewTag("TAG_MOVE_DB_FROM_FOO", "暂不支持");
            matchTextWithViewTag("TAG_MOVE_DB_FROM_BAR", "暂不支持");
        }
    }

    @Test
    public void testDeleteDatabase() {
        matchTextWithViewTag(
                "TAG_DELETE_DB_FOO",
                "success"
        );
        matchTextWithViewTag(
                "TAG_DELETE_DB_BAR",
                "success"
        );
    }

    @Test
    public void testGetDatabasePath() {
        String hostDatabasePath = ApplicationProvider.getApplicationContext().getDatabasePath("test").getParentFile().getAbsolutePath();
        matchTextWithViewTag(
                "TAG_GET_DATABASE_PATH_FOO",
                hostDatabasePath + "/" + EXPECT_NAME + "_foo"
        );
        matchTextWithViewTag(
                "TAG_GET_DATABASE_PATH_BAR",
                hostDatabasePath + "/" + EXPECT_NAME + "_bar"
        );
    }

    @Test
    public void testDatabaseList() {
        String dbName = EXPECT_NAME + "_bar";
        matchSubstringWithViewTag(
                "TAG_DATABASE_LIST",
                dbName
        );
    }

    private void assertTextAndFileExist(String viewTag, String text) {
        matchTextWithViewTag(viewTag, text);
        Assert.assertTrue("文件应该存在", new File(text).exists());
    }
}
