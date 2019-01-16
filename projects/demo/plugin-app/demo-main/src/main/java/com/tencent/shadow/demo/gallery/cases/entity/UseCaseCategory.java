package com.tencent.shadow.demo.gallery.cases.entity;

import java.util.ArrayList;
import java.util.List;

public class UseCaseCategory {

    public int id;

    public String title;

    public List<UseCase> caseList = new ArrayList<>();

    public UseCaseCategory(int id, String title) {
        this.id = id;
        this.title = title;
    }
}
