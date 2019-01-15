package com.tencent.shadow.demo.main.cases.entity;

import java.util.ArrayList;
import java.util.List;

public class TestCategory {

    public int id;

    public String title;

    public List<TestCase> caseList = new ArrayList<>();

    public TestCategory(int id, String title) {
        this.id = id;
        this.title = title;
    }
}
