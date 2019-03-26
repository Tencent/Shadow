package test;

import android.content.ComponentName;
import android.content.pm.PackageManager;

public class TestPackageManager {

    void test1(){
        PackageManager packageManager = new PackageManager();

        packageManager.getApplicationInfo("test",0);
        packageManager.getActivityInfo(new ComponentName(),0);
    }


    void test2(){
        new Inner(){
            @Override
            void run() {
                PackageManager packageManager = new PackageManager();

                packageManager.getApplicationInfo("test",0);
                packageManager.getActivityInfo(new ComponentName(),0);

                new Inner(){
                    @Override
                    void run() {
                        PackageManager packageManager = new PackageManager();

                        packageManager.getApplicationInfo("test",0);
                        packageManager.getActivityInfo(new ComponentName(),0);

                    }
                };
            }
        };
    }


    class Inner{
        void run(){}
    }

}
