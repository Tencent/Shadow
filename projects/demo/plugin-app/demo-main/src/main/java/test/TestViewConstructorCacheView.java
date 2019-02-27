package test;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 测试同名View构造器缓存冲突的View
 */
public class TestViewConstructorCacheView extends View {
    public TestViewConstructorCacheView(Context context) {
        super(context);
    }

    public TestViewConstructorCacheView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
