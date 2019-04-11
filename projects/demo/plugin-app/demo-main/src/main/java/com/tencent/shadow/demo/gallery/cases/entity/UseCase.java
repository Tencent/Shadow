package com.tencent.shadow.demo.gallery.cases.entity;

import android.os.Bundle;

public class UseCase {

    public String name;

    public String summary;

    public Class pageClass;

    public Bundle bundle;

    public UseCase(String name, String summary, Class pageClass) {
        this.name = name;
        this.summary = summary;
        this.pageClass = pageClass;
    }
}
