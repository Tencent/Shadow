package test.fragment;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.shadow.core.runtime.ShadowActivity;

public class UseStartActivityFragment {

    ShadowActivity test(TestFragment fragment) {
        fragment.startActivity(new Intent());
        fragment.startActivity(new Intent(), new Bundle());
        return fragment.getActivity();
    }
}
