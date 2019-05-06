package com.tencent.shadow.demo.usecases.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.demo.gallery.R;
import com.tencent.shadow.demo.gallery.cases.entity.UseCase;

import dalvik.system.PathClassLoader;
import test.TestViewConstructorCacheView;

public class TestViewConstructorCache extends Activity {

    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "同名View构造器缓存冲突测试";
        }

        @Override
        public String getSummary() {
            return "宿主和插件具有同名View应该都能正常加载各自的版本";
        }

        @Override
        public Class getPageClass() {
            return TestViewConstructorCache.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_view_cons_cache);
        TestViewConstructorCacheView testView = findViewById(R.id.testView);

        PathClassLoader pathClassLoader = (PathClassLoader) getApplication().getBaseContext().getClass().getClassLoader();

        boolean assertTrue;
        try {
            assertTrue = pathClassLoader.loadClass(TestViewConstructorCacheView.class.getName()) != testView.getClass();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("宿主中应该也有同名View");
        }

        if (!assertTrue) {
            throw new AssertionError("插件和宿主中不应该能加载出相同View名的同一个类");
        }
    }
}
