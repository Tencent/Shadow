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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.packagemanager;

import static android.content.pm.PackageManager.GET_META_DATA;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.view.ViewGroup;

import com.tencent.shadow.test.plugin.general_cases.lib.gallery.util.UiUtil;
import com.tencent.shadow.test.plugin.general_cases.lib.usecases.service.TestService;

import java.util.List;

public class TestPackageManagerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup viewGroup = UiUtil.setActivityContentView(this);

        getApplicationInfo(viewGroup);
        getActivityInfo(viewGroup);
        getServiceInfo(viewGroup);
        getPackageInfo(viewGroup);
        queryContentProviders(viewGroup);
        resolveActivityByExplicitIntent(viewGroup);
    }

    private void getApplicationInfo(ViewGroup viewGroup) {
        String className;
        String nativeLibraryDir;
        String metaData;
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), GET_META_DATA);
            className = applicationInfo.className;
            nativeLibraryDir = applicationInfo.nativeLibraryDir;
            metaData = applicationInfo.metaData != null ? applicationInfo.metaData.getString("test_meta") : null;
        } catch (PackageManager.NameNotFoundException e) {
            className = nativeLibraryDir = metaData = "NameNotFoundException";
        }
        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "getApplicationInfo/className",
                        "getApplicationInfo/className",
                        className
                )
        );
        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "getApplicationInfo/nativeLibraryDir",
                        "getApplicationInfo/nativeLibraryDir",
                        nativeLibraryDir
                )
        );
        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "getApplicationInfo/metaData",
                        "getApplicationInfo/metaData",
                        metaData
                )
        );
    }

    private void getActivityInfo(ViewGroup viewGroup) {
        String name;
        String packageName;
        try {
            ActivityInfo activityInfo = getPackageManager().getActivityInfo(new ComponentName(this, this.getClass()), 0);
            name = activityInfo.name;
            packageName = activityInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            name = packageName = "NameNotFoundException";
        }
        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "getActivityInfo/name",
                        "getActivityInfo/name",
                        name
                )
        );
        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "getActivityInfo/packageName",
                        "getActivityInfo/packageName",
                        packageName
                )
        );
    }

    private void getServiceInfo(ViewGroup viewGroup) {
        String name;
        String packageName;
        try {
            ServiceInfo serviceInfo = getPackageManager().getServiceInfo(new ComponentName(this, TestService.class), 0);
            name = serviceInfo.name;
            packageName = serviceInfo.packageName;
        } catch (PackageManager.NameNotFoundException e) {
            name = packageName = "NameNotFoundException";
        }
        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "getServiceInfo/name",
                        "getServiceInfo/name",
                        name
                )
        );
        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "getServiceInfo/packageName",
                        "getServiceInfo/packageName",
                        packageName
                )
        );
    }

    private void getPackageInfo(ViewGroup viewGroup) {
        String versionName;
        String versionCode;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = Integer.toString(packageInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            versionName = versionCode = "NameNotFoundException";
        }
        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "getPackageInfo/versionName",
                        "getPackageInfo/versionName",
                        versionName
                )
        );
        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "getPackageInfo/versionCode",
                        "getPackageInfo/versionCode",
                        versionCode
                )
        );
    }

    private void queryContentProviders(ViewGroup viewGroup) {
        PackageManager packageManager = getPackageManager();
        ApplicationInfo applicationInfo = getApplicationInfo();
        String processName = applicationInfo.processName;
        int uid = applicationInfo.uid;
        List<ProviderInfo> providerInfos = packageManager.queryContentProviders(processName, uid, PackageManager.MATCH_ALL);

        String size = providerInfos.size() > 0 ? ">0" : "0";

        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "queryContentProviders/size",
                        "queryContentProviders/size",
                        size
                )
        );
    }

    private void resolveActivityByExplicitIntent(ViewGroup viewGroup) {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(this, this.getClass());
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        String name;
        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            name = resolveInfo.activityInfo.name;
        } else {
            name = "";
        }
        viewGroup.addView(
                UiUtil.makeItem(
                        this,
                        "resolveActivity/explicit",
                        "resolveActivity/explicit",
                        name
                )
        );
    }
}
