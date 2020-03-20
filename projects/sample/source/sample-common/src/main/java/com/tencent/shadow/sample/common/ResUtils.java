package com.tencent.shadow.sample.common;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

public class ResUtils {

    public static String getString(int id) {
        return ApplicationHelper.getInstance().getResources().getString(id);
    }

    public static String getString(int id, Object... formatArgs) {
        return ApplicationHelper.getInstance().getString(id, formatArgs);
    }

    public static int getColor(int id) {
        return ApplicationHelper.getInstance().getResources().getColor(id);
    }

    public static Drawable getDrawable(int id) {
        return ApplicationHelper.getInstance().getResources().getDrawable(id);
    }

    public static int getDimenPx(int resId) {
        return ApplicationHelper.getInstance().getResources().getDimensionPixelSize(resId);
    }

    public static ColorStateList getColorStateList(int id) {
        return ApplicationHelper.getInstance().getResources().getColorStateList(id);
    }

    public static int getIdentifier(String name, String defType) {
        return ApplicationHelper.getInstance().getResources().getIdentifier(name, defType,
                ApplicationHelper.getInstance().getApplicationContext().getPackageName());
    }
}
