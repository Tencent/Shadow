package android.app;

import com.tencent.cubershi.mock_interface.MockActivity;

/**
 * 用于替换系统的Activity类.它不用定义任何行为,只是为了将MockActivity改个名字,以便继承自它的类承认它.
 *
 * @author cubershi
 */
public class Activity extends MockActivity {
}
