package com.huawei.rtcdemo;

import android.app.Application;
import android.content.SharedPreferences;

import com.huawei.rtc.HRTCEngine;
import com.huawei.rtc.IHRTCEngineEventHandler;
import com.huawei.rtc.models.HRTCLogInfo;
import com.huawei.rtcdemo.adapter.RtcEventCallbackAdapter;
import com.huawei.rtcdemo.adapter.RtcEventHandler;
import com.huawei.rtcdemo.bean.JoomRoomFailure;
import com.huawei.rtcdemo.bean.UserJoinedEvent;
import com.huawei.rtcdemo.utils.KLog;
import com.huawei.rtcdemo.utils.PrefManager;

import java.util.concurrent.CopyOnWriteArrayList;

import static com.huawei.rtc.models.HRTCLogInfo.HRTCLogLevel.HRTC_LOG_LEVEL_DEBUG;

public class RtcApplication extends Application {
    private HRTCEngine mHwRtcEngine;
    private RtcEventCallbackAdapter mHwHandler = new RtcEventCallbackAdapter();
    String serverAddr;
    String appId;
    SharedPreferences rtcSp;

    public RtcApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rtcSp = PrefManager.getPreferences(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        HRTCEngine.destroy();
    }

    public HRTCEngine getEngine() {
        if (mHwRtcEngine == null) {
            HRTCLogInfo logInfo = new HRTCLogInfo();
            logInfo.setLevel(HRTC_LOG_LEVEL_DEBUG);
            logInfo.setPath(KLog.getLogPath());
            HRTCEngine.setLogParam(true, logInfo);

            serverAddr = rtcSp.getString(Constants.RTC_PREF_SERVER_ADDR, Constants.RTC_DEFAULT_SERVER_ADDR);
            appId = Constants.RTC_DEFAULT_APP_ID;
            mHwRtcEngine = HRTCEngine.create(getApplicationContext(), serverAddr, appId, mHwHandler);
        }

        return mHwRtcEngine;
    }

    public void registerEventHandler(RtcEventHandler handler) {
        mHwHandler.addHandler(handler);
    }

    public void removeEventHandler(RtcEventHandler handler) {
        mHwHandler.removeHandler(handler);
    }

    public CopyOnWriteArrayList<UserJoinedEvent> getUserJoinedEvents() {
        return mHwHandler.getUserJoinedEvents();
    }

    public CopyOnWriteArrayList<JoomRoomFailure> getJoomRoomFailure() {
        return mHwHandler.getJoomRoomFailure();
    }

    public void clearCachedEvents() {
        mHwHandler.clearCachedEvents();
    }
}
