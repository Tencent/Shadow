package com.tencent.shadow.core.runtime;

import android.view.InputQueue;
import android.view.SurfaceHolder;
import android.view.ViewTreeObserver;

import com.tencent.shadow.core.runtime.container.HostActivityDelegator;
import com.tencent.shadow.core.runtime.container.HostNativeActivityDelegator;

public class ShadowNativeActivity extends ShadowActivity implements SurfaceHolder.Callback2,
        InputQueue.Callback, ViewTreeObserver.OnGlobalLayoutListener {

    private HostNativeActivityDelegator hostNativeActivityDelegator;

    @Override
    public void setHostActivityDelegator(HostActivityDelegator delegator) {
        super.setHostActivityDelegator(delegator);
        hostNativeActivityDelegator = (HostNativeActivityDelegator) delegator;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        hostNativeActivityDelegator.superSurfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        hostNativeActivityDelegator.superSurfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceRedrawNeeded(SurfaceHolder holder) {
        hostNativeActivityDelegator.superSurfaceRedrawNeeded(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hostNativeActivityDelegator.superSurfaceDestroyed(holder);
    }

    @Override
    public void onInputQueueCreated(InputQueue queue) {
        hostNativeActivityDelegator.superOnInputQueueCreated(queue);
    }

    @Override
    public void onInputQueueDestroyed(InputQueue queue) {
        hostNativeActivityDelegator.superOnInputQueueDestroyed(queue);
    }

    @Override
    public void onGlobalLayout() {
        hostNativeActivityDelegator.superOnGlobalLayout();
    }
}
