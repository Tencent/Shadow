package android.app;

import com.tencent.shadow.core.runtime.ShadowActivity;

/**
 * 因为在TransformManager中ActivityTransform是在DialogTransform之前进行的，
 * 所以DialogTransform运行时，Activity已经都变成ShadowActivity了，
 * 所以这个Mock类定义的方法中Activity已经是ShadowActivity了。
 */
public class Dialog {
    public final void setOwnerActivity(ShadowActivity activity) {
    }

    public final ShadowActivity getOwnerActivity() {
        return null;
    }
}
