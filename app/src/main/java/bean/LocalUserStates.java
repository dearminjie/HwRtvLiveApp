package com.huawei.rtcdemo.bean;

import static com.huawei.rtc.models.HRTCUserInfo.HRTCRoleType.HRTC_ROLE_TYPE_JOINER;

public class LocalUserStates {
    private int mRole = HRTC_ROLE_TYPE_JOINER.ordinal();
    private String mChannelId;
    private String mUserId;
    private String mMaxBitrate;
    private int mThreshold = 3;
    private int streamSelect;

    public int getmRole() {
        return mRole;
    }

    public void setmRole(int mRole) {
        this.mRole = mRole;
    }

    public String getmChannelId() {
        return mChannelId;
    }

    public void setmChannelId(String mChannelId) {
        this.mChannelId = mChannelId;
    }

    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public String getmMaxBitrate() {
        return mMaxBitrate;
    }

    public void setmMaxBitrate(String mMaxBitrate) {
        this.mMaxBitrate = mMaxBitrate;
    }

    public int getmThreshold() {
        return mThreshold;
    }

    public void setmThreshold(int mThreshold) {
        this.mThreshold = mThreshold;
    }

    public int getStreamSelect() {
        return streamSelect;
    }

    public void setStreamSelect(int streamSelect) {
        this.streamSelect = streamSelect;
    }
}
