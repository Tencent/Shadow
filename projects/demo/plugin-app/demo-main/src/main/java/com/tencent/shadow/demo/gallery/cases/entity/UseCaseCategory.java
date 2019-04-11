package com.tencent.shadow.demo.gallery.cases.entity;

import java.util.ArrayList;
import java.util.List;

public class UseCaseCategory {

    public String title;

    public List<UseCase> caseList = new ArrayList<>();

    public UseCaseCategory(String title) {
        this.title = title;
    }
}
