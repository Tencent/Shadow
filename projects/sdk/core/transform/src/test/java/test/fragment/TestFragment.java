package test.fragment;

import android.app.Fragment;
import android.content.Context;

public class TestFragment extends Fragment {

    @Override
    public void onAttach(Context context) {
        System.out.println("before super.onAttach" + context);
        super.onAttach(context);
        System.out.println("after super.onAttach" + context);
    }

}
