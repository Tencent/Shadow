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

package test;

import android.content.ComponentName;
import android.content.pm.PackageManager;

public class TestPackageManager {

    void test1(){
        PackageManager packageManager = new PackageManager();

        packageManager.getApplicationInfo("test",0);
        packageManager.getActivityInfo(new ComponentName(),0);
    }


    void test2(){
        new Inner(){
            @Override
            void run() {
                PackageManager packageManager = new PackageManager();

                packageManager.getApplicationInfo("test",0);
                packageManager.getActivityInfo(new ComponentName(),0);

                new Inner(){
                    @Override
                    void run() {
                        PackageManager packageManager = new PackageManager();

                        packageManager.getApplicationInfo("test",0);
                        packageManager.getActivityInfo(new ComponentName(),0);

                    }
                };
            }
        };
    }


    class Inner{
        void run(){}
    }

}
