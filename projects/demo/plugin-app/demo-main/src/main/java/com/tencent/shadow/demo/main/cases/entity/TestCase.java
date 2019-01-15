package com.tencent.shadow.demo.main.cases.entity;

public class TestCase {

    public int id;

    public String name;

    public String summary;

    public Class pageClass;

    public TestCase(int id, String name, String summary, Class pageClass) {
        this.id = id;
        this.name = name;
        this.summary = summary;
        this.pageClass = pageClass;
    }
}
