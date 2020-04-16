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

public class ProgrammaticallyAddFragmentActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_fragment_activity);

        String fragmentType = getIntent().getStringExtra("FragmentType");
        addFragmentProgrammatically(fragmentType);
    }

    private void addFragmentProgrammatically(String fragmentType) {


        TestFragment testFragment;
        switch (fragmentType) {
            case "TestNormalFragment":
                testFragment = new TestNormalFragment();
                break;
            case "TestSubFragment":
                testFragment = new TestSubFragment();
                break;
            case "TestBaseFragment":
                testFragment = new SubTestBaseFragment();
                break;
            case "TestDialogFragment":
                testFragment = new TestDialogFragment();
                break;
            case "OnlyOverrideActivityMethodBaseFragment":
                testFragment = new TestSubOnlyOverrideOnAttachActivityFragment();
                break;
            case "OnlyOverrideContextMethodBaseFragment":
                testFragment = new TestSubOnlyOverrideOnAttachContextFragment();
                break;
            default:
                throw new IllegalArgumentException("fragmentType不识别：" + fragmentType);
        }
        testFragment.setTestArguments("addFragmentProgrammatically");
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, (Fragment) testFragment, "TestFragmentTag")
                .commit();
    }
}
