package com.tencent.shadow.test.plugin.general_cases.lib.usecases.context;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity.UseCase;

public class ActivityContextSubDirTestActivity extends SubDirContextThemeWrapperTestActivity {
    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "ActivityContextSubDir测试";
        }

        @Override
        public String getSummary() {
            return "测试Activity作为Context因BusinessName不同而隔离的相关特性";
        }

        @Override
        public Class getPageClass() {
            return ActivityContextSubDirTestActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fillTestValues(this);
    }
}
