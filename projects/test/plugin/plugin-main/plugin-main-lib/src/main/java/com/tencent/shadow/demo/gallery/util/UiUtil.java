package com.tencent.shadow.demo.gallery.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

final public class UiUtil {
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
