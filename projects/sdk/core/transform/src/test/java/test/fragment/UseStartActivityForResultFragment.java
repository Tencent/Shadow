package test.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.shadow.core.runtime.ShadowActivity;

public class UseStartActivityForResultFragment {

    ShadowActivity test(TestFragment fragment) {
        fragment.startActivityForResult(new Intent(), 1);
        fragment.startActivityForResult(new Intent(), 1, new Bundle());
        return fragment.getActivity();
    }
}
