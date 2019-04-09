package test;

import android.app.Application;

public class TestApplication extends Application {

    Application get() {
        System.out.println("get Application");
        return this;
    }
}