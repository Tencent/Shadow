package android.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.shadow.core.runtime.ShadowActivity;

/**
 * 因为在TransformManager中ActivityTransform是在FragmentSupportTransform之前进行的，
 * 所以FragmentSupportTransform运行时，Activity已经都变成ShadowActivity了，
 * 所以这个Mock类定义的方法中Activity已经是ShadowActivity了。
 */
public class Fragment {
    final public ShadowActivity getActivity() {
        return null;
    }

    public Context getContext() {
        return null;
    }

    final public Object getHost() {
        return null;
    }

    public void startActivity(Intent intent) {

    }

    public void startActivity(Intent intent, Bundle options) {

    }

    public void startActivityForResult(Intent intent, int requestCode) {

    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {

    }

    public void onAttach(Context context) {

    }
}
