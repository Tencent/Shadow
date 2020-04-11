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

package com.tencent.shadow.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

final public class UiUtil {
    private static ViewGroup makeItemViewGroup(Context viewContext) {
        LinearLayout linearLayout = new LinearLayout(viewContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }

    private static ScrollView wrapScrollView(View view) {
        ScrollView scrollView = new ScrollView(view.getContext());
        scrollView.addView(view);
        return scrollView;
    }

    @SuppressLint("SetTextI18n")
    public static ViewGroup makeItemView(Context viewContext, String labelText, String viewTag) {
        TextView label = new TextView(viewContext);
        label.setText(labelText + ":");
        label.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));

        TextView value = new TextView(viewContext);
        value.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        value.setTag(viewTag);

        LinearLayout linearLayout = new LinearLayout(viewContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(0, 10, 0, 10);

        linearLayout.addView(label);
        linearLayout.addView(value);
        return linearLayout;
    }

    public static void setItemValue(ViewGroup viewGroupContainsItem, String viewTag, String value) {
        TextView textView = viewGroupContainsItem.findViewWithTag(viewTag);
        textView.setText(value);
    }

    public static ViewGroup makeItem(
            Context viewContext,
            String labelText,
            final String viewTag,
            String value
    ) {
        final ViewGroup itemView = makeItemView(viewContext, labelText, viewTag);
        setItemValue(itemView, viewTag, value);
        return itemView;
    }

    public static ViewGroup makeItem(
            Context viewContext,
            String labelText,
            final String viewTag,
            AsyncGetValue asyncGetValue
    ) {
        final ViewGroup itemView = makeItemView(viewContext, labelText, viewTag);
        asyncGetValue.getValue(new AsyncGetValueCallback() {
            @Override
            public void onGotValue(String value) {
                setItemValue(itemView, viewTag, value);
            }
        });
        return itemView;
    }

    interface AsyncGetValue {
        void getValue(AsyncGetValueCallback callback);
    }

    interface AsyncGetValueCallback {
        void onGotValue(String value);
    }
}
