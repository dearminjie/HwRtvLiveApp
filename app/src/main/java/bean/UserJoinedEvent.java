package com.huawei.rtcdemo.bean;

public class UserJoinedEvent {
    private String s;
    private String s1;
    private String s2;

    public UserJoinedEvent(String s, String s1, String s2){
        this.s = s;
        this.s1 = s1;
        this.s2 = s2;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getS1() {
        return s1;
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }

    public String getS2() {
        return s2;
    }

    public void setS2(String s2) {
        this.s2 = s2;
    }
}
