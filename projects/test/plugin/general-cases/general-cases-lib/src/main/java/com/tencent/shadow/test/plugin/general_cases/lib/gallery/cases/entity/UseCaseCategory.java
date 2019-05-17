package com.tencent.shadow.test.plugin.general_cases.lib.gallery.cases.entity;

public class UseCaseCategory {

    public String title;

    public UseCase[] caseList ;

    public UseCaseCategory(String title,UseCase[] caseList) {
        this.title = title;
        this.caseList = caseList;
    }
}
