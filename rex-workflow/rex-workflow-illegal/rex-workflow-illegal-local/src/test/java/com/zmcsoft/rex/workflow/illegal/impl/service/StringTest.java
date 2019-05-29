package com.zmcsoft.rex.workflow.illegal.impl.service;

public class StringTest {
    public static void main(String[] args) {
        String signDept = "123456";
        int length = signDept.length();

        String add = "000000000000";

        String signDepts = signDept+add;

        String substring = signDepts.substring(0,12);
        System.out.println(substring);
    }
}
