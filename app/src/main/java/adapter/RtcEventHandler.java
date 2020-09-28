package com.huawei.rtcdemo.adapter;

import com.huawei.rtc.models.HRTCConnectInfo;
import com.huawei.rtc.models.HRTCLocalAudioStats;
import com.huawei.rtc.models.HRTCLocalVideoStats;
import com.huawei.rtc.models.HRTCRemoteAudioStats;
import com.huawei.rtc.models.HRTCRemoteVideoStats;
import com.huawei.rtc.models.HRTCStatsInfo;
import com.huawei.rtc.models.HRTCUserInfo;
import com.huawei.rtc.utils.HRTCEnums;

import java.util.List;

public interface RtcEventHandler {
    void onWarning(int var1, String var2);

    void onError(int var1, String var2);

    void onJoinRoomSuccess(String var1, String var2);

    void onJoinRoomFailure(int var1, String var2);

    void onLeaveRoom(HRTCEnums.HRTCLeaveReason reason, HRTCStatsInfo statsInfo);

    void onConnectOtherRoom(HRTCConnectInfo var1, int var2, String var3);

    void onDisconnectOtherRoom(HRTCConnectInfo var1, int var2, String var3);

    void onUserJoined(String var1, String var2, String var3);

    void onUserOffline(String var1, String var2, int var3);

    void onFirstRemoteVideoDecoded(String var1, String var2, int var3, int var4);

    void onConnectionStateChange(HRTCEnums.HRTCConnStateTypes var1, HRTCEnums.HRTCConnChangeReason var2, String var3);

    void onLogUploadResult(int var1);

    void onLogUploadProgress(int var1);

    void onAgreedStreamAvailable(String var1, String var2, HRTCEnums.HRTCStreamType var3);

    void onUserRoleChanged(String var1, HRTCUserInfo.HRTCRoleType var2, HRTCUserInfo.HRTCRoleType var3);

    void onAudioRouteChanged(HRTCEnums.HRTCAudioRoute var1);

    void onVideoStats(List<HRTCLocalVideoStats> var1, List<HRTCRemoteVideoStats> var2);

    void onAudioStats(List<HRTCLocalAudioStats> var1, List<HRTCRemoteAudioStats> var2);

    void onUserSubStreamAvailable(String var1, String var2, boolean var3);

    void onSubStreamStats(List<HRTCLocalVideoStats> var1, List<HRTCRemoteVideoStats> var2);

    void onRenderVideoFrame(String var1, String var2, byte[] var3, int var4, int var5, int var6, int var7);

    void onPlaybackAudioFrame(String var1, byte[] var2, int var3, int var4, int var5, int var6, int var7);

    void onRemoteAudioStateChanged(String userId, HRTCEnums.HRTCRemoteAudioStreamState state, HRTCEnums.HRTCRemoteAudioStreamStateReason reason);

    void onRemoteVideoStateChanged(String userId, HRTCEnums.HRTCRemoteVideoStreamState state, HRTCEnums.HRTCRemoteVideoStreamStateReason reason);

    void onSignatureExpired();
}
