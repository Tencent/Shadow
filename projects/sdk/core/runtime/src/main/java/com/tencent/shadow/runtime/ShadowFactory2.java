package com.tencent.shadow.runtime;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * 具备创建自定义View功能的Factory2
 *
 * // TODO #36 实现LayoutInflater 的 filter功能
 */
public class ShadowFactory2 implements LayoutInflater.Factory2 {

    private String mPartKey;

    private final Object[] mConstructorArgs = new Object[2];

    private final Class<?>[] mConstructorSignature = new Class[]{
            Context.class, AttributeSet.class};

    private final HashMap<String, Constructor<? extends View>> sConstructorMap =
            new HashMap<String, Constructor<? extends View>>();


    private LayoutInflater mLayoutInflater;


    private static final HashMap<String, String> sCreateSystemMap = new HashMap<>();


    public ShadowFactory2(String partKey, LayoutInflater layoutInflater) {
        mPartKey = partKey;
        mLayoutInflater = layoutInflater;
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view;
        if (name.contains(".")) {//自定义view
            if (sCreateSystemMap.get(name) == null) {
                sCreateSystemMap.put(name, mPartKey);
            }
            try {
                view = createCustomView(name, context, attrs);
            } catch (Exception e) {
                view = null;
            }
        } else {
            if (context instanceof PluginActivity) {//fragment的构造在activity中
                view = ((PluginActivity) context).onCreateView(parent, name, context, attrs);
            } else {
                view = null;
            }
        }
        return view;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }


    private View createCustomView(String name, Context context, AttributeSet attrs) {
        String cacheKey = mPartKey + name;
        Constructor<? extends View> constructor = sConstructorMap.get(cacheKey);
        if (constructor != null && !verifyClassLoader(context, constructor)) {
            constructor = null;
            sConstructorMap.remove(cacheKey);
        }
        Class<? extends View> clazz = null;

        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);

                constructor = clazz.getConstructor(mConstructorSignature);
                constructor.setAccessible(true);
                sConstructorMap.put(cacheKey, constructor);
            }

            Object lastContext = mConstructorArgs[0];
            if (mConstructorArgs[0] == null) {
                // Fill in the context if not already within inflation.
                mConstructorArgs[0] = context;
            }
            Object[] args = mConstructorArgs;
            args[1] = attrs;

            final View view = constructor.newInstance(args);
            if (view instanceof ViewStub && Build.VERSION.SDK_INT >= 16) {
                // Use the same context when inflating ViewStub later.
                final ViewStub viewStub = (ViewStub) view;
                viewStub.setLayoutInflater(mLayoutInflater.cloneInContext((Context) args[0]));
            }
            mConstructorArgs[0] = lastContext;
            return view;
        } catch (Exception e) {
            return null;
        }
    }


    private static final ClassLoader BOOT_CLASS_LOADER = LayoutInflater.class.getClassLoader();

    private final boolean verifyClassLoader(Context context, Constructor<? extends View> constructor) {
        final ClassLoader constructorLoader = constructor.getDeclaringClass().getClassLoader();
        if (constructorLoader == BOOT_CLASS_LOADER) {
            // fast path for boot class loader (most common case?) - always ok
            return true;
        }
        // in all normal cases (no dynamic code loading), we will exit the following loop on the
        // first iteration (i.e. when the declaring classloader is the contexts class loader).
        ClassLoader cl = context.getClassLoader();
        do {
            if (constructorLoader == cl) {
                return true;
            }
            cl = cl.getParent();
        } while (cl != null);
        return false;
    }




}
