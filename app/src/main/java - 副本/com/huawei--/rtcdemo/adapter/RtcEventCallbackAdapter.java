package com.huawei.rtcdemo.adapter;

import com.huawei.rtc.IHRTCEngineEventHandler;
import com.huawei.rtc.models.HRTCConnectInfo;
import com.huawei.rtc.models.HRTCLocalAudioStats;
import com.huawei.rtc.models.HRTCLocalVideoStats;
import com.huawei.rtc.models.HRTCRemoteAudioStats;
import com.huawei.rtc.models.HRTCRemoteVideoStats;
import com.huawei.rtc.models.HRTCStatsInfo;
import com.huawei.rtc.models.HRTCUserInfo;
import com.huawei.rtc.utils.HRTCEnums;
import com.huawei.rtcdemo.bean.JoomRoomFailure;
import com.huawei.rtcdemo.bean.UserJoinedEvent;
import com.huawei.rtcdemo.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RtcEventCallbackAdapter extends IHRTCEngineEventHandler {
    private static final String TAG = "RtcEventHandler";
    private ArrayList<RtcEventHandler> mHandler = new ArrayList<>();
    //解决主界面加入房间，在主界面回调的问题
    private CopyOnWriteArrayList<UserJoinedEvent> userJoinedEvents = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<JoomRoomFailure> joomRoomFailures = new CopyOnWriteArrayList<>();

    @Override
    public void onWarning(int s, String s1) {
        for (RtcEventHandler handler : mHandler) {
            handler.onWarning(s, s1);
        }
    }

    @Override
    public void onError(int s, String s1) {
        for (RtcEventHandler handler : mHandler) {
            handler.onError(s, s1);
        }
    }

    @Override
    public void onJoinRoomSuccess(String s, String s1) {
        for (RtcEventHandler handler : mHandler) {
            handler.onJoinRoomSuccess(s, s1);
        }
    }

    @Override
    public void onJoinRoomFailure(int i, String s) {
        boolean isContainLiveActivity = false;
        for (RtcEventHandler handler : mHandler) {
            if (handler.getClass().getName().equals("com.huawei.rtcdemo.activities.LiveActivity")) {
                isContainLiveActivity = true;
            }
            handler.onJoinRoomFailure(i, s);
        }
        if (!isContainLiveActivity) {
            joomRoomFailures.add(new JoomRoomFailure(i, s));
            LogUtil.i(TAG, "onUserJoined userJoinedEvent s: " + s + "s:" + s);
        }
    }

    @Override
    public void onLeaveRoom(HRTCEnums.HRTCLeaveReason reason, HRTCStatsInfo statsInfo) {
        for (RtcEventHandler handler : mHandler) {
            handler.onLeaveRoom(reason, statsInfo);
        }
    }

    @Override
    public void onUserJoined(String s, String s1, String s2) {
        boolean isContainLiveActivity = false;
        for (RtcEventHandler handler : mHandler) {
            if (handler.getClass().getName().equals("com.huawei.rtcdemo.activities.LiveActivity")) {
                isContainLiveActivity = true;
            }
            handler.onUserJoined(s, s1, s2);
        }
        if (!isContainLiveActivity) {
            userJoinedEvents.add(new UserJoinedEvent(s, s1, s2));
            LogUtil.i(TAG, "onUserJoined userJoinedEvent s: " + s + "s1:" + s1 + "s2:" + s2);
        }
    }

    @Override
    public void onUserOffline(String s, String s1, int i) {
        for (RtcEventHandler handler : mHandler) {
            handler.onUserOffline(s, s1, i);
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(String s, String s1, int i, int i1) {
        for (RtcEventHandler handler : mHandler) {
            handler.onFirstRemoteVideoDecoded(s, s1, i, i1);
        }
    }

    @Override
    public void onAgreedStreamAvailable(String s, String s1, HRTCEnums.HRTCStreamType streamType) {
        for (RtcEventHandler handler : mHandler) {
            handler.onAgreedStreamAvailable(s, s1, streamType);
        }
    }

    @Override
    public void onConnectionStateChange(HRTCEnums.HRTCConnStateTypes i, HRTCEnums.HRTCConnChangeReason i1, String s) {
        for (RtcEventHandler handler : mHandler) {
            handler.onConnectionStateChange(i, i1, s);
        }
    }

    @Override
    public void onLogUploadResult(int i) {
        for (RtcEventHandler handler : mHandler) {
            handler.onLogUploadResult(i);
        }
    }

    @Override
    public void onLogUploadProgress(int i) {
        for (RtcEventHandler handler : mHandler) {
            handler.onLogUploadProgress(i);
        }
    }

    @Override
    public void onAudioRouteChanged(HRTCEnums.HRTCAudioRoute audioRoute) {
        for (RtcEventHandler handler : mHandler) {
            handler.onAudioRouteChanged(audioRoute);
        }
    }

    @Override
    public void onVideoStats(List<HRTCLocalVideoStats> list, List<HRTCRemoteVideoStats> list1) {
        for (RtcEventHandler handler : mHandler) {
            handler.onVideoStats(list, list1);
        }
    }

    @Override
    public void onAudioStats(List<HRTCLocalAudioStats> list, List<HRTCRemoteAudioStats> list1) {
        for (RtcEventHandler handler : mHandler) {
            handler.onAudioStats(list, list1);
        }
    }

    @Override
    public void onUserSubStreamAvailable(String s, String s1, boolean b) {
        for (RtcEventHandler handler : mHandler) {
            handler.onUserSubStreamAvailable(s, s1, b);
        }
    }

    @Override
    public void onRenderVideoFrame(String s, String s1, byte[] bytes, int i, int i1, int i2, int i3) {
        for (RtcEventHandler handler : mHandler) {
            handler.onRenderVideoFrame(s, s1, bytes, i, i1, i2, i3);
        }
    }

    @Override
    public void onPlaybackAudioFrame(String s, byte[] bytes, int i, int i1, int i2, int i3, int i4) {
        for (RtcEventHandler handler : mHandler) {
            handler.onPlaybackAudioFrame(s, bytes, i, i1, i2, i3, i4);
        }
    }

    @Override
    public void onSignatureExpired() {

    }

    @Override
    public void onSubStreamStats(List<HRTCLocalVideoStats> localStats, List<HRTCRemoteVideoStats> remoteStats) {
        for (RtcEventHandler handler : mHandler) {
            handler.onSubStreamStats(localStats, remoteStats);
        }
    }

    @Override
    public void onConnectOtherRoom(HRTCConnectInfo connectInfo, int error, String msg) {
        for (RtcEventHandler handler : mHandler) {
            handler.onConnectOtherRoom(connectInfo, error, msg);
        }
    }

    @Override
    public void onDisconnectOtherRoom(HRTCConnectInfo connectInfo, int error, String msg) {
        for (RtcEventHandler handler : mHandler) {
            handler.onDisconnectOtherRoom(connectInfo, error, msg);
        }
    }

    @Override
    public void onUserRoleChanged(String roomId, HRTCUserInfo.HRTCRoleType oldRole, HRTCUserInfo.HRTCRoleType newRole) {
        for (RtcEventHandler handler : mHandler) {
            handler.onUserRoleChanged(roomId, oldRole, newRole);
        }
    }

    @Override
    public void onRemoteAudioStateChanged(String userId, HRTCEnums.HRTCRemoteAudioStreamState state, HRTCEnums.HRTCRemoteAudioStreamStateReason reason) {
        for (RtcEventHandler handler : mHandler) {
            handler.onRemoteAudioStateChanged(userId, state, reason);
        }
    }

    @Override
    public void onRemoteVideoStateChanged(String userId, HRTCEnums.HRTCRemoteVideoStreamState state, HRTCEnums.HRTCRemoteVideoStreamStateReason reason) {
        for (RtcEventHandler handler : mHandler) {
            handler.onRemoteVideoStateChanged(userId, state, reason);
        }
    }

    public void addHandler(RtcEventHandler handler) {
        mHandler.add(handler);
    }

    public void removeHandler(RtcEventHandler handler) {
        mHandler.remove(handler);
    }

    public CopyOnWriteArrayList<UserJoinedEvent> getUserJoinedEvents() {
        return userJoinedEvents;
    }

    public CopyOnWriteArrayList<JoomRoomFailure> getJoomRoomFailure() {
        return joomRoomFailures;
    }

    public void clearCachedEvents() {
        joomRoomFailures.clear();
        userJoinedEvents.clear();
    }
}
