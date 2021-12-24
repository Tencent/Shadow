package android.view;

/**
 * mock for test
 */
public interface WindowManager {
    interface LayoutParams {
        int SOFT_INPUT_STATE_UNSPECIFIED = 0;
        int SOFT_INPUT_STATE_UNCHANGED = 1;
        int SOFT_INPUT_STATE_HIDDEN = 2;
        int SOFT_INPUT_STATE_ALWAYS_HIDDEN = 3;
        int SOFT_INPUT_STATE_VISIBLE = 4;
        int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5;
        int SOFT_INPUT_MASK_ADJUST = 0xf0;
        int SOFT_INPUT_ADJUST_UNSPECIFIED = 0x00;
        int SOFT_INPUT_ADJUST_RESIZE = 0x10;
        int SOFT_INPUT_ADJUST_PAN = 0x20;
        int SOFT_INPUT_ADJUST_NOTHING = 0x30;
        int SOFT_INPUT_IS_FORWARD_NAVIGATION = 0x100;
    }
}
