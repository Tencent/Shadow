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

package com.tencent.shadow.test.lib.constant;

final public class Constant {
    public static final String KEY_PLUGIN_ZIP_PATH = "pluginZipPath";
    public static final String KEY_ACTIVITY_CLASSNAME = "KEY_ACTIVITY_CLASSNAME";
    public static final String KEY_EXTRAS = "KEY_EXTRAS";
    public static final String KEY_PLUGIN_PART_KEY = "KEY_PLUGIN_PART_KEY";
    public static final String KEY_FROM_ID = "KEY_FROM_ID";
    public static final String PART_KEY_PLUGIN_MAIN_APP = "test-plugin-general-cases";
    public static final String PART_KEY_PLUGIN_SERVICE_FOR_HOST = "plugin-service-for-host";
    public static final String PART_KEY_PLUGIN_ANDROIDX = "test-plugin-androidx-cases";
    public static final int FROM_ID_NOOP = 1000;
    public static final int FROM_ID_START_ACTIVITY = 1002;
    public static final int FROM_ID_BIND_SERVICE = 1003;
    public static final int FROM_ID_REINSTALL_PLUGIN = 1004;
}
