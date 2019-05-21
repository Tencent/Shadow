package com.tencent.shadow.core.manager.installplugin;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.tencent.shadow.core.manager.installplugin.InstalledPluginDBHelper.DB_NAME_PREFIX;
import static com.tencent.shadow.core.manager.test.R.raw;

/**
 * 数据库兼容性测试
 * <p>
 * 这项测试在写测试用例前，先回滚代码到旧版本，利用正常的config.json(如res/raw/plugin1.json)通过旧版本的
 * InstalledDao安装，生成旧版本的数据库。然后用{@link DbCompatibilityTest#dumpDbToFile(File)}方法
 * 将该旧数据库导出成sql文件上库(如res/raw/init_sql_version1.sql)。
 * 注意：PRAGMA user_version不能dump出来，需要手工写。
 * 再准备一个升级过后数据库dump出来的sql（如res/raw/expect_sql_version1.sql)作为Assert expected。
 */
@RunWith(AndroidJUnit4.class)
public class DbCompatibilityTest {
    private static final String TEST_DB_NAME = "DbCompatibilityTest";

    private File databasePath;
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        databasePath = context.getDatabasePath(
                InstalledPluginDBHelper.DB_NAME_PREFIX + TEST_DB_NAME
        );
    }

    @Test
    public void testDbNamePrefixNotChanged() {
        Assert.assertEquals("DB名前缀不能改",
                "shadow_installed_plugin_db",
                DB_NAME_PREFIX
        );
    }

    @Test
    public void testCompatibleWithVersion1()
            throws IOException, InterruptedException, TimeoutException {
        testCompatibleWithVersion(
                raw.init_sql_version1,
                raw.expect_sql_version1
        );
    }

    @Test
    public void testCompatibleWithVersion2()
            throws IOException, InterruptedException, TimeoutException {
        testCompatibleWithVersion(
                raw.init_sql_version2,
                raw.expect_sql_version2
        );
    }

    @Test
    public void testCompatibleWithVersion3()
            throws IOException, InterruptedException, TimeoutException {
        testCompatibleWithVersion(
                raw.init_sql_version3,
                raw.expect_sql_version3
        );
    }

    @Test
    public void testCompatibleWithVersion4()
            throws IOException, InterruptedException, TimeoutException {
        testCompatibleWithVersion(
                raw.init_sql_version4,
                raw.expect_sql_version4
        );
    }

    //    @Test //取消注释以生成文件
    public void generateCurrentVersionInitSqlFile() throws Exception {
        initDbWithConfigJson(raw.plugin1);
    }

    private void initDbWithConfigJson(int... configJsonResId)
            throws IOException, JSONException, TimeoutException, InterruptedException {
        List<String> configs = new LinkedList<>();
        for (int resId : configJsonResId) {
            File configJsonFile = getResRawFile(resId);
            String configJson = FileUtils.readFileToString(configJsonFile, Charset.defaultCharset());
            FileUtils.forceDelete(configJsonFile);
            configs.add(configJson);
        }


        InstalledPluginDBHelper dbHelper = new InstalledPluginDBHelper(context, TEST_DB_NAME);
        InstalledDao installedDao = new InstalledDao(dbHelper);

        for (String configJson : configs) {
            installedDao.insert(PluginConfig.parseFromJson(configJson, context.getCacheDir()));
        }

        dbHelper.close();

        File dumpSqlFile = File.createTempFile("initDbWithConfigJson", ".sql", context.getExternalCacheDir());
        dumpDbToFile(dumpSqlFile);

        Assert.assertTrue(dumpSqlFile.exists() && dumpSqlFile.length() > 0);

        throw new RuntimeException("执行命令复制文件到电脑:adb pull " + dumpSqlFile.getAbsolutePath());
    }

    private void testCompatibleWithVersion(int initSqlResId, int expectSqlResId)
            throws IOException, InterruptedException, TimeoutException {
        File initSqlFile = getResRawFile(initSqlResId);
        initDb(initSqlFile);

        InstalledPluginDBHelper dbHelper = new InstalledPluginDBHelper(context, TEST_DB_NAME);
        dbHelper.getReadableDatabase();//只是为了触发onUpgrade方法
        dbHelper.close();

        File dumpSqlFile = File.createTempFile("dumpDb", ".sql");
        dumpDbToFile(dumpSqlFile);

        File exceptSqlFile = getResRawFile(expectSqlResId);

        try {
            Assert.assertEquals(
                    FileUtils.readLines(exceptSqlFile, Charset.defaultCharset()),
                    FileUtils.readLines(dumpSqlFile, Charset.defaultCharset())
            );
        } finally {
            FileUtils.forceDelete(dumpSqlFile);
            FileUtils.forceDelete(initSqlFile);
            FileUtils.forceDelete(exceptSqlFile);
            deleteDb();
        }
    }

    private File getResRawFile(int resId) throws IOException {
        InputStream is = context.getResources().openRawResource(resId);
        File resRawFile = File.createTempFile("getResRawFile", null);
        IOUtils.copy(is, new FileOutputStream(resRawFile));
        return resRawFile;
    }

    /**
     * 初始化数据库
     * 用于将数据库通过sql脚本直接初始化成旧版本数据库。
     * 旧版本数据库的sql采用手工执行sqlite3的.dump命令生成的。
     */
    private void initDb(File initSqlFile)
            throws IOException, InterruptedException, TimeoutException {
        String[] cmd = {
                "sqlite3",
                "-init",
                initSqlFile.getAbsolutePath(),
                databasePath.getAbsolutePath(),
                ".exit"
        };
        Process p = Runtime.getRuntime().exec(cmd);

        boolean timeout = !p.waitFor(10, TimeUnit.SECONDS);

        if (timeout) {
            throw new TimeoutException("exec超时");
        }
    }

    /**
     * 将当前数据库Dump成sql文件
     */
    private void dumpDbToFile(File dumpSqlFile)
            throws InterruptedException, IOException, TimeoutException {
        File tempDumpCmdArgsFile = File.createTempFile("dumpDb", ".cmd");

        PrintWriter pw = new PrintWriter(new FileWriter(tempDumpCmdArgsFile));
        pw.println(".output " + dumpSqlFile.getAbsolutePath());
        pw.println(".dump " + InstalledPluginDBHelper.TABLE_NAME_MANAGER);
        pw.println(".exit");
        pw.flush();
        pw.close();

        String[] cmd = {
                "sqlite3",
                databasePath.getAbsolutePath(),
                ".read " + tempDumpCmdArgsFile.getAbsolutePath()
        };

        Process p = Runtime.getRuntime().exec(cmd, null, context.getCacheDir());
        boolean timeout = !p.waitFor(10, TimeUnit.SECONDS);

        FileUtils.forceDelete(tempDumpCmdArgsFile);

        if (timeout) {
            throw new TimeoutException("exec超时");
        }

        Assert.assertTrue(dumpSqlFile.exists() && dumpSqlFile.length() > 0);
    }

    /**
     * 删除数据库。
     * 应该在每一个测试用例最后调用此方法删除数据库，避免对下一个测试用例有影响。
     */
    private void deleteDb() throws IOException {
        FileUtils.forceDelete(databasePath);
    }
}
