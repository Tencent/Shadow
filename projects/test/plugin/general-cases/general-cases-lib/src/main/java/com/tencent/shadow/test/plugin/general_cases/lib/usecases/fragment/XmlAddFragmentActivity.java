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

package com.tencent.shadow.test.plugin.general_cases.lib.usecases.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.tencent.shadow.test.plugin.general_cases.lib.R;

public class XmlAddFragmentActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String fragmentType = getIntent().getStringExtra("FragmentType");
        int xmlId;
        switch (fragmentType) {
            case "TestNormalFragment":
                xmlId = R.layout.layout_xml_add_normal_fragment_activity;
                break;
            case "TestSubFragment":
                xmlId = R.layout.layout_xml_add_sub_fragment_activity;
                break;
            case "TestBaseFragment":
                xmlId = R.layout.layout_xml_add_base_fragment_activity;
                break;
            default:
                throw new IllegalArgumentException("fragmentType不识别：" + fragmentType);
        }

        setContentView(xmlId);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if ("TestFragmentTag".equals(fragment.getTag())) {
            TestFragment testFragment = (TestFragment) fragment;
            testFragment.setTestArguments("addFragmentWithXml");
        }
    }
}