package com.huawei.rtcdemo.bean;

public class BeanRoomMember {

    private String userId;
    private boolean isPlaying = true;
    private boolean isWithAux = false;
    private boolean isAudioOPen = true;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean getIsPlaying() {
        return isPlaying;
    }

    public void setIsPlaying(boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    public boolean isWithAux() {
        return isWithAux;
    }

    public void setWithAux(boolean withAux) {
        isWithAux = withAux;
    }

    public boolean getIsAudioOPen() {
        return isAudioOPen;
    }

    public void setAudioOPen(boolean audioOPen) {
        isAudioOPen = audioOPen;
    }

    public BeanRoomMember(String userId, boolean isPlaying, boolean isWithAux) {
        this.userId = userId;
        this.isPlaying = isPlaying;
        this.isWithAux = isWithAux;
    }
}
