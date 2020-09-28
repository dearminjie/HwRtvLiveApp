package com.huawei.rtcdemo.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.rtc.models.HRTCConnectInfo;
import com.huawei.rtc.models.HRTCLocalAudioStats;
import com.huawei.rtc.models.HRTCLocalVideoStats;
import com.huawei.rtc.models.HRTCRemoteAudioStats;
import com.huawei.rtc.models.HRTCRemoteVideoStats;
import com.huawei.rtc.models.HRTCStatsInfo;
import com.huawei.rtc.models.HRTCUserInfo;
import com.huawei.rtc.utils.HRTCEnums;
import com.huawei.rtcdemo.adapter.RtcEventHandler;
import com.huawei.rtcdemo.utils.WindowUtil;

import java.util.List;


public class BaseActivity extends AppCompatActivity implements RtcEventHandler {
    protected int mStatusBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtil.hideWindowStatusBar(getWindow());
        initStatusBarHeight();
    }

    protected void onGlobalLayoutCompleted() {
    }

    private void initStatusBarHeight() {
        mStatusBarHeight = WindowUtil.getSystemStatusBarHeight(this);
    }


    @Override
    public void onWarning(int i, String s) {

    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void onJoinRoomSuccess(String s, String s1) {

    }

    @Override
    public void onJoinRoomFailure(int i, String s) {

    }

    @Override
    public void onLeaveRoom(HRTCEnums.HRTCLeaveReason reason, HRTCStatsInfo statsInfo) {

    }

    @Override
    public void onUserJoined(String s, String s1, String s2) {

    }

    @Override
    public void onUserOffline(String s, String s1, int i) {

    }

    @Override
    public void onFirstRemoteVideoDecoded(String s, String s1, int i, int i1) {

    }

    @Override
    public void onConnectionStateChange(HRTCEnums.HRTCConnStateTypes connStateTypes, HRTCEnums.HRTCConnChangeReason connChangeReason, String s) {

    }

    @Override
    public void onLogUploadResult(int i) {

    }

    @Override
    public void onLogUploadProgress(int i) {

    }

    @Override
    public void onAgreedStreamAvailable(String s, String s1, HRTCEnums.HRTCStreamType streamType) {

    }

    @Override
    public void onAudioRouteChanged(HRTCEnums.HRTCAudioRoute audioRoute) {

    }

    @Override
    public void onVideoStats(List<HRTCLocalVideoStats> list, List<HRTCRemoteVideoStats> list1) {

    }

    @Override
    public void onAudioStats(List<HRTCLocalAudioStats> list, List<HRTCRemoteAudioStats> list1) {

    }

    @Override
    public void onUserSubStreamAvailable(String s, String s1, boolean b) {

    }

    @Override
    public void onSubStreamStats(List<HRTCLocalVideoStats> localStats, List<HRTCRemoteVideoStats> remoteStats) {

    }

    @Override
    public void onRenderVideoFrame(String s, String s1, byte[] bytes, int i, int i1, int i2, int i3) {

    }

    @Override
    public void onPlaybackAudioFrame(String s, byte[] bytes, int i, int i1, int i2, int i3, int i4) {

    }

    @Override
    public void onSignatureExpired() {

    }

    @Override
    public void onConnectOtherRoom(HRTCConnectInfo connectInfo, int error, String msg) {

    }

    @Override
    public void onDisconnectOtherRoom(HRTCConnectInfo connectInfo, int error, String msg) {

    }

    @Override
    public void onUserRoleChanged(String roomId, HRTCUserInfo.HRTCRoleType oldRole, HRTCUserInfo.HRTCRoleType newRole) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onRemoteAudioStateChanged(String userId, HRTCEnums.HRTCRemoteAudioStreamState state, HRTCEnums.HRTCRemoteAudioStreamStateReason reason) {
    }

    @Override
    public void onRemoteVideoStateChanged(String userId, HRTCEnums.HRTCRemoteVideoStreamState state, HRTCEnums.HRTCRemoteVideoStreamStateReason reason) {
    }
}
