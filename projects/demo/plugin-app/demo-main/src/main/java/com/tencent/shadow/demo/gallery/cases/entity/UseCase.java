package com.tencent.shadow.demo.gallery.cases.entity;

import android.os.Bundle;

public class UseCase {

    public int id;

    public String name;

    public String summary;

    public Class pageClass;

    public Bundle bundle;

    public UseCase(int id, String name, String summary, Class pageClass) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.pageClass = pageClass;
    }
}
