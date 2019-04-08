package test;

import android.app.Service;

public class TestService extends Service {

    Service getService() {
        System.out.println("getService");
        return this;
    }
}