package com.tencent.shadow.runtime;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class MixResources extends ResourcesWrapper {

    private Resources mHostResources;

    public MixResources(Resources hostResources,Resources pluginResources) {
        super(pluginResources);
        mHostResources = hostResources;
    }

    @Override
    public CharSequence getText(int id) throws NotFoundException {
        return super.getText(id);
    }

    @Override
    public String getString(int id) throws NotFoundException {
        return super.getString(id);
    }

    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        return super.getString(id, formatArgs);
    }

    @Override
    public float getDimension(int id) throws NotFoundException {
        return super.getDimension(id);
    }

    @Override
    public int getDimensionPixelOffset(int id) throws NotFoundException {
        return super.getDimensionPixelOffset(id);
    }

    @Override
    public int getDimensionPixelSize(int id) throws NotFoundException {
        return super.getDimensionPixelSize(id);
    }

    @Override
    public Drawable getDrawable(int id) throws NotFoundException {
        try {
            return super.getDrawable(id);
        } catch (NotFoundException e) {
            return mHostResources.getDrawable(id);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getDrawable(int id, Theme theme) throws NotFoundException {
        try {
            return super.getDrawable(id, theme);
        } catch (NotFoundException e) {
            return mHostResources.getDrawable(id,theme);
        }
    }

    @Override
    public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
        try {
            return super.getDrawableForDensity(id, density);
        } catch (NotFoundException e) {
            return mHostResources.getDrawableForDensity(id,density);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getDrawableForDensity(int id, int density, Theme theme) {
        try {
            return super.getDrawableForDensity(id, density, theme);
        } catch (Exception e) {
            return mHostResources.getDrawableForDensity(id,density,theme);
        }
    }

    @Override
    public int getColor(int id) throws NotFoundException {
        return super.getColor(id);
    }

    @Override
    public int getColor(int id, Theme theme) throws NotFoundException {
        return super.getColor(id, theme);
    }

    @Override
    public ColorStateList getColorStateList(int id) throws NotFoundException {
        return super.getColorStateList(id);
    }

    @Override
    public ColorStateList getColorStateList(int id, Theme theme) throws NotFoundException {
        return super.getColorStateList(id, theme);
    }

    @Override
    public boolean getBoolean(int id) throws NotFoundException {
        return super.getBoolean(id);
    }

    @Override
    public XmlResourceParser getLayout(int id) throws NotFoundException {
        try {
            return super.getLayout(id);
        } catch (NotFoundException e) {
           return mHostResources.getLayout(id);
        }
    }

    @Override
    public String getResourceName(int resid) throws NotFoundException {
        return super.getResourceName(resid);
    }
}
