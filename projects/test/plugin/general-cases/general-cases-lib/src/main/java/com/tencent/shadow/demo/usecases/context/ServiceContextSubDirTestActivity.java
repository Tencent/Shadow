package com.tencent.shadow.demo.usecases.context;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import androidx.test.espresso.IdlingRegistry;

import com.tencent.shadow.demo.gallery.cases.entity.UseCase;
import com.tencent.shadow.demo.usecases.SimpleIdlingResource;
import com.tencent.shadow.demo.usecases.service.TestService;

public class ServiceContextSubDirTestActivity extends SubDirContextThemeWrapperTestActivity {
    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "ServiceContextSubDir测试";
        }

        @Override
        public String getSummary() {
            return "测试Service作为Context因BusinessName不同而隔离的相关特性";
        }

        @Override
        public Class getPageClass() {
            return ServiceContextSubDirTestActivity.class;
        }
    }

    final private SimpleIdlingResource mIdlingResource = new SimpleIdlingResource();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIdlingResource.setIdleState(false);
        IdlingRegistry.getInstance().register(mIdlingResource);

        Intent intent = new Intent(this, TestService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                TestService.MyLocalServiceBinder binder = (TestService.MyLocalServiceBinder) service;
                TestService testService = binder.getMyLocalService();
                fillTestValues(testService);
                mIdlingResource.setIdleState(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IdlingRegistry.getInstance().unregister(mIdlingResource);
    }
}
