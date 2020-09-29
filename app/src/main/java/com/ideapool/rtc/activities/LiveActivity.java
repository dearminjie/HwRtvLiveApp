package com.ideapool.rtc.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.rtc.HRTCEngine;
import com.huawei.rtc.models.HRTCLocalAudioStats;
import com.huawei.rtc.models.HRTCLocalVideoStats;
import com.huawei.rtc.models.HRTCRemoteAudioStats;
import com.huawei.rtc.models.HRTCRemoteVideoStats;
import com.huawei.rtc.models.HRTCStatsInfo;
import com.huawei.rtc.models.HRTCUserInfo;
import com.huawei.rtc.utils.HRTCConstants;
import com.huawei.rtc.utils.HRTCEnums;
import com.ideapool.rtc.Constants;
import com.ideapool.rtc.R;
import com.ideapool.rtc.RtcApplication;
import com.ideapool.rtc.adapter.RoomMembersAdapter;
import com.ideapool.rtc.bean.BeanRoomMember;
import com.ideapool.rtc.bean.JoomRoomFailure;
import com.ideapool.rtc.bean.LocalUserStates;
import com.ideapool.rtc.bean.UserJoinedEvent;
import com.ideapool.rtc.ui.UploadDialog;
import com.ideapool.rtc.ui.WndArrangeMaker;
import com.ideapool.rtc.utils.LogUtil;
import com.ideapool.rtc.utils.PrefManager;
import com.ideapool.rtc.utils.SreenPropertyUtil;
import com.ideapool.rtc.activities.BaseActivity;
import com.zyyoona7.lib.EasyPopup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.huawei.rtc.models.HRTCUserInfo.HRTCRoleType.HRTC_ROLE_TYPE_JOINER;
import static com.huawei.rtc.models.HRTCUserInfo.HRTCRoleType.HRTC_ROLE_TYPE_PLAYER;
import static com.huawei.rtc.models.HRTCUserInfo.HRTCRoleType.HRTC_ROLE_TYPE_PUBLISER;
import static com.huawei.rtc.utils.HRTCEnums.HRTCAudioRoute.HRTC_AUDIO_ROUTE_BLUETOOTH;
import static com.huawei.rtc.utils.HRTCEnums.HRTCAudioRoute.HRTC_AUDIO_ROUTE_HEADSET;
import static com.huawei.rtc.utils.HRTCEnums.HRTCAudioRoute.HRTC_AUDIO_ROUTE_RECEIVER;
import static com.huawei.rtc.utils.HRTCEnums.HRTCAudioRoute.HRTC_AUDIO_ROUTE_SPEAKER;
import static com.huawei.rtc.utils.HRTCEnums.HRTCRotationType.HRTC_ROTATION_TYPE_0;
import static com.huawei.rtc.utils.HRTCEnums.HRTCRotationType.HRTC_ROTATION_TYPE_180;
import static com.huawei.rtc.utils.HRTCEnums.HRTCRotationType.HRTC_ROTATION_TYPE_270;
import static com.huawei.rtc.utils.HRTCEnums.HRTCRotationType.HRTC_ROTATION_TYPE_90;
import static com.huawei.rtc.utils.HRTCEnums.HRTCSpeakerModel.HRTC_SPEAKER_MODE_EARPIECE;
import static com.huawei.rtc.utils.HRTCEnums.HRTCSpeakerModel.HRTC_SPEAKER_MODE_SPEAKER;
import static com.huawei.rtc.utils.HRTCEnums.HRTCStreamType.HRTC_STREAM_TYPE_HD;

public class LiveActivity extends BaseActivity {
    private static final String TAG = "LiveActivity";
    private WndArrangeMaker mWndArrangeMaker;
    private ImageView mSwitchCameraBtn;
    private ImageView mMuteRoleChangeBtn;
    private ImageView mMuteAudioBtn;
    private ImageView mMuteVideoBtn;
    private ImageView mAudioRouteBtn;
    private ImageView mHungupBtn;
    private EasyPopup mCirclePop;
    private ImageView roomListBtn;
    private TextView roomListBackTv;
    private TextView membersTv;
    private RecyclerView roomMemberRecyclerView;
    private List<BeanRoomMember> roommemberBeansList = new ArrayList<>();
    private RoomMembersAdapter commentRecyclerAdapter;

    private HRTCEngine mHwRtcEngine;
    private LocalUserStates localUserStates = new LocalUserStates();
    SharedPreferences rtcSp;

    private boolean mLeaveManually = false;
    private boolean mOnPauseCalled = false;

    private int toastBottomMargin;//提示框位置

    private UploadDialog uploadDialog;
    private ImageView logUploadBtn;

    private OrientationEventListener mOrientationEventListener;
    private int rotation = 0;
    private boolean isMuteAll = false;
   private WebView mWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.i(TAG, "onCreate !");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_room);

        rtcSp = PrefManager.getPreferences(getApplicationContext());

        // 获取屏幕高度
        toastBottomMargin = getWindowManager().getDefaultDisplay().getHeight() / 7;
        localUserStates.setmChannelId(getIntent().getStringExtra(Constants.KEY_CHANNELID));
        localUserStates.setmRole(getIntent().getIntExtra(Constants.KEY_ROLE, HRTC_ROLE_TYPE_PLAYER.ordinal()));
        localUserStates.setmUserId(getIntent().getStringExtra(Constants.KEY_USERID));
        localUserStates.setStreamSelect(rtcSp.getInt(Constants.RTC_PREF_STREAM_SELECT, HRTC_STREAM_TYPE_HD.ordinal()));

        if (localUserStates.getmRole() == HRTC_ROLE_TYPE_PLAYER.ordinal()) {
            localUserStates.setmThreshold(4);
        }

        mHwRtcEngine = ((RtcApplication) getApplication()).getEngine();
        ((RtcApplication) getApplication()).registerEventHandler(this);

        initUI();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initOrientationListener();

        final WeakReference<LiveActivity> activityWeakReference = new WeakReference<>(this);
        this.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                if (!((RtcApplication) getApplication()).getUserJoinedEvents().isEmpty()) {
                    CopyOnWriteArrayList<UserJoinedEvent> userJoinedEvents = ((RtcApplication) getApplication()).getUserJoinedEvents();

                    for (UserJoinedEvent userJoinedEvent : userJoinedEvents) {
                        activityWeakReference.get().onUserJoined(userJoinedEvent.getS(), userJoinedEvent.getS1(), userJoinedEvent.getS2());
                        userJoinedEvents.remove(userJoinedEvent);
                        LogUtil.i(TAG, "onCreate userJoinedEvent s: " + userJoinedEvent.getS() + "  s1:" + userJoinedEvent.getS1() + "  s2:" + userJoinedEvent.getS2());
                    }
                }
            }
        });

        this.getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!((RtcApplication) getApplication()).getJoomRoomFailure().isEmpty()) {
                    CopyOnWriteArrayList<JoomRoomFailure> joomRoomFailures = ((RtcApplication) getApplication()).getJoomRoomFailure();
                    for (JoomRoomFailure joomRoomFailure : joomRoomFailures) {
                        joomRoomFailures.remove(joomRoomFailure);
                        LogUtil.i(TAG, "onCreate userJoinedEvent i: " + joomRoomFailure.getI() + "  s:" + joomRoomFailure.getS());
                        Toast toast = Toast.makeText(LiveActivity.this, "JoomRoomFailure i: " + joomRoomFailure.getI() + " msg：" + joomRoomFailure.getS(), Toast.LENGTH_SHORT);
                        setToast(toast);
                    }
                    finish();
                }
            }
        }, 100);
    }

    private void initOrientationListener() {
        mOrientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                LogUtil.i(TAG, "onOrientationChanged: " + orientation);
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    // 手机平放时，检测不到有效的角度
                    if (rotation != 0) {
                        mHwRtcEngine.setRemoteSubStreamViewRotation(getAuxUserid(), HRTC_ROTATION_TYPE_0);
                    }
                    rotation = 0;
                    return;
                }
                //只检测是否有四个角度的改变
                if (orientation > 350 || orientation < 10) {
                    // 0度：手机默认竖屏状态（home键在正下方）
                    Log.d(TAG, "下");
                    if (rotation != 0) {
                        mHwRtcEngine.setRemoteSubStreamViewRotation(getAuxUserid(), HRTC_ROTATION_TYPE_0);
                    }
                    rotation = 0;
                } else if (orientation > 80 && orientation < 100) {
                    // 90度：手机顺时针旋转90度横屏（home建在左侧）
                    Log.d(TAG, "左");
                    if (rotation != 270) {
                        mHwRtcEngine.setRemoteSubStreamViewRotation(getAuxUserid(), HRTC_ROTATION_TYPE_270);
                    }
                    rotation = 270;
                } else if (orientation > 170 && orientation < 190) {
                    // 180度：手机顺时针旋转180度竖屏（home键在上方）
                    Log.d(TAG, "上");
                    if (rotation != 180) {
                        mHwRtcEngine.setRemoteSubStreamViewRotation(getAuxUserid(), HRTC_ROTATION_TYPE_180);
                    }
                    rotation = 180;
                } else if (orientation > 260 && orientation < 280) {
                    // 270度：手机顺时针旋转270度横屏，（home键在右侧）
                    Log.d(TAG, "右");
                    if (rotation != 90) {
                        mHwRtcEngine.setRemoteSubStreamViewRotation(getAuxUserid(), HRTC_ROTATION_TYPE_90);
                    }
                    rotation = 90;
                }
            }
        };
    }

    private void initUI() {

        TextView roomName = findViewById(R.id.live_room_name);
        TextView userID = findViewById(R.id.live_room_broadcaster_uid);
        roomName.setText("room id：" + localUserStates.getmChannelId());
        userID.setText("user id：" + localUserStates.getmUserId());
        roomName.setSelected(true);
        userID.setSelected(true);

        initUserIcon();

        boolean isPlayer = (localUserStates.getmRole() == HRTC_ROLE_TYPE_PLAYER.ordinal());

        if (uploadDialog == null) {
            uploadDialog = new UploadDialog(this);
        }

        logUploadBtn = findViewById(R.id.live_btn_log_upload);
        uploadDialog.setCanceledOnTouchOutside(true);

        mSwitchCameraBtn = findViewById(R.id.live_btn_switch_camera);
        mSwitchCameraBtn.setActivated(!isPlayer);

        mMuteRoleChangeBtn = findViewById(R.id.live_btn_role_change);
        mMuteRoleChangeBtn.setActivated(!isPlayer);

        mMuteVideoBtn = findViewById(R.id.live_btn_mute_video);
        mMuteVideoBtn.setActivated(true);

        mMuteAudioBtn = findViewById(R.id.live_btn_mute_audio);
        mMuteAudioBtn.setActivated(true);

        mAudioRouteBtn = findViewById(R.id.live_btn_audio_route);
        mAudioRouteBtn.setActivated(false);

        mHungupBtn = findViewById(R.id.live_btn_hangup);

        roomListBtn = findViewById(R.id.live_btn_roomlist);

        mWndArrangeMaker = findViewById(R.id.live_video_grid_layout);

        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient()
        {
            public boolean shouldOverrideUrlLoading(WebView view, String url)
           {
                mWebView.loadUrl(url);
                return true;
            }
       });
        mWebView.loadUrl("http://www.baidu.com");

        if (localUserStates.getmRole() == HRTC_ROLE_TYPE_JOINER.ordinal()) {
            startJoin();
        } else if (localUserStates.getmRole() == HRTC_ROLE_TYPE_PLAYER.ordinal()) {
            startPlay();
        } else {

            startPublish();
        }
    }

    private SurfaceView prepareRtcVideo(String userId, boolean local, HRTCEnums.HRTCStreamType type) {
        LogUtil.i(TAG, "prepareRtcVideo userId:" + userId + "local:" + local + "type:" + type);
        SurfaceView surface = HRTCEngine.createRenderer(getApplicationContext());
        if (local) {
            mHwRtcEngine.setupLocalView(surface, HRTCEnums.HRTCVideoDisplayMode.HRTC_VIDEO_DISPLAY_MODE_HIDDEN);
        } else {
            int ret = mHwRtcEngine.startRemoteStreamView(userId, surface, type);
            if (ret == 0) {
                mHwRtcEngine.setRemoteViewDisplayMode(userId, HRTCEnums.HRTCVideoDisplayMode.HRTC_VIDEO_DISPLAY_MODE_HIDDEN);
            }
        }
        return surface;
    }

    private void removeRtcVideo(String userId, boolean local) {
        LogUtil.i(TAG, "removeRtcVideo userId:" + userId + "local:" + local);
        if (local) {
            mHwRtcEngine.setupLocalView(null, HRTCEnums.HRTCVideoDisplayMode.HRTC_VIDEO_DISPLAY_MODE_HIDDEN);
        } else {
            mHwRtcEngine.stopRemoteStreamView(userId);
        }

    }

    public void getRoomMemberPopup(View view) {
        WindowManager wm = (WindowManager) getBaseContext()
                .getSystemService(Context.WINDOW_SERVICE);

        //计算屏幕宽高
        SreenPropertyUtil.getAndroiodScreenProperty(this);

        int width = wm.getDefaultDisplay().getWidth();
        mCirclePop = new EasyPopup(LiveActivity.this)
                .setContentView(R.layout.activity_room_list)
                //      .setAnimationStyle(R.style.CirclePopAnim)
                //是否允许点击PopupWindow之外的地方消失
                .setFocusAndOutsideEnable(true)
                //允许背景变暗
                .setBackgroundDimEnable(true)
                //变暗的透明度(0-1)，0为完全透明
                .setDimValue(1f)
                .setWidth(width)
                .setHeight(SreenPropertyUtil.getHeight_xp(1f))
                .createPopup();
        mCirclePop.showAtLocation(roomListBtn, Gravity.BOTTOM, 0, 0);

        roomMemberRecyclerView = mCirclePop.getView(R.id.recycler_comment);

        roomListBackTv = mCirclePop.getView(R.id.tv_room_list_back);
        membersTv = mCirclePop.getView(R.id.tv_members);
        membersTv.setText("(" + roommemberBeansList.size() + ")");
        roomListBackTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCirclePop.dismiss();
            }
        });

        final ImageView audioImg = mCirclePop.getView(R.id.img_audio);
        audioImg.setImageResource(isMuteAll ? R.drawable.mic_off : R.drawable.mic_on);
        audioImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuteAll = !isMuteAll;
                mHwRtcEngine.muteAllRemoteAudio(isMuteAll);
                audioImg.setImageResource(isMuteAll ? R.drawable.mic_off : R.drawable.mic_on);
                for (BeanRoomMember beanRoomMember : roommemberBeansList) {
                    beanRoomMember.setAudioOPen(!isMuteAll);
                }
                if (commentRecyclerAdapter != null) {
                    commentRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                getRoomMemberListWithThread();
            }
        }).start();

    }

    private void getRoomMemberListWithThread() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //此时已在主线程中，可以更新UI了
                // 定义一个线性布局管理器
                LinearLayoutManager manager = new LinearLayoutManager(LiveActivity.this);
                // 设置布局管理器
                roomMemberRecyclerView.setLayoutManager(manager);
                LogUtil.i(TAG, "roommemberBeansList.size(): " + roommemberBeansList.size());
                // 设置adapter
                commentRecyclerAdapter = new RoomMembersAdapter(LiveActivity.this, roommemberBeansList, localUserStates.getmThreshold());
                roomMemberRecyclerView.setAdapter(commentRecyclerAdapter);
                // 增加分割线
                roomMemberRecyclerView.addItemDecoration(new DividerItemDecoration(LiveActivity.this, RecyclerView.VERTICAL));//support 包的版本是 25或以上时使用
            }
        });
    }

    private void initUserIcon() {
        Bitmap origin = BitmapFactory.decodeResource(getResources(), R.drawable.my);
        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), origin);
        drawable.setCircular(true);
        ImageView iconView = findViewById(R.id.live_name_board_icon);
        iconView.setImageDrawable(drawable);
    }

    @Override
    protected void onGlobalLayoutCompleted() {
        RelativeLayout topLayout = findViewById(R.id.live_room_top_layout);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) topLayout.getLayoutParams();
        params.height = mStatusBarHeight + topLayout.getMeasuredHeight();
        topLayout.setLayoutParams(params);
        topLayout.setPadding(0, mStatusBarHeight, 0, 0);
    }

    private void startJoin() {
        LogUtil.i(TAG, "startJoin!");
        SurfaceView surface = prepareRtcVideo(localUserStates.getmUserId(), true, HRTCEnums.HRTCStreamType.values()[localUserStates.getStreamSelect()]);
        mWndArrangeMaker.addUserVideoSurface(localUserStates.getmUserId(), surface, true);
        mAudioRouteBtn.setActivated(false);
        mHwRtcEngine.setSpeakerModel(HRTC_SPEAKER_MODE_EARPIECE);
        roomListBtn.setActivated(true);
    }

    private void startPlay() {
        LogUtil.i(TAG, "startPlay !");
        mMuteAudioBtn.setEnabled(false);
        mAudioRouteBtn.setActivated(false);
        mHwRtcEngine.setSpeakerModel(HRTC_SPEAKER_MODE_EARPIECE);
        mMuteVideoBtn.setEnabled(false);
        mSwitchCameraBtn.setEnabled(false);
        roomListBtn.setActivated(true);
    }

    private void startPublish() {
        LogUtil.i(TAG, "startPublish !");
        SurfaceView surface = prepareRtcVideo(localUserStates.getmUserId(), true, HRTCEnums.HRTCStreamType.values()[localUserStates.getStreamSelect()]);
        mWndArrangeMaker.addUserVideoSurface(localUserStates.getmUserId(), surface, true);
        mMuteRoleChangeBtn.setEnabled(false);
        mAudioRouteBtn.setEnabled(false);
        roomListBtn.setEnabled(false);
    }

    public void renderRemoteUser(String userId, HRTCEnums.HRTCStreamType type) {
        LogUtil.i(TAG, "HwRtcDemo renderRemoteUser userId:" + userId + "type:" + type);

        SurfaceView surface = prepareRtcVideo(userId, false, type);
        mWndArrangeMaker.addUserVideoSurface(userId, surface, false);
    }

    public void renderRemoteAuxView(String userId) {
        LogUtil.i(TAG, "renderRemoteAuxView userId:" + userId);
        SurfaceView surface = HRTCEngine.createRenderer(getApplicationContext());
        mHwRtcEngine.startRemoteSubStreamView(userId, surface);
        mHwRtcEngine.setRemoteSubStreamViewDisplayMode(userId, HRTCEnums.HRTCVideoDisplayMode.HRTC_VIDEO_DISPLAY_MODE_FIT);

        mWndArrangeMaker.addUserVideoSurface("AUX_" + userId, surface, false);
    }

    public void unSelectAllRemoteUsers() {
        LogUtil.i(TAG, "unSelectAllRemoteUsers !");
        Iterator it = roommemberBeansList.iterator();
        while (it.hasNext()) {
            BeanRoomMember beanRoomMember = (BeanRoomMember) it.next();
            if (isUserPlaying(beanRoomMember.getUserId())) {
                removeRtcVideo(beanRoomMember.getUserId(), false);
                mWndArrangeMaker.removeUserVideo(beanRoomMember.getUserId());
                beanRoomMember.setIsPlaying(false);
            }
        }
    }

    public void reRenderAllRemoteUsers() {
        LogUtil.i(TAG, "reRenderAllRemoteUsers !");
        Iterator it = roommemberBeansList.iterator();
        while (it.hasNext() && getNumberOfPlaying() < localUserStates.getmThreshold()) {
            BeanRoomMember beanRoomMember = (BeanRoomMember) it.next();
            renderRemoteUser(beanRoomMember.getUserId(), HRTCEnums.HRTCStreamType.HRTC_STREAM_TYPE_HD);
            beanRoomMember.setIsPlaying(true);
        }
    }

    private void removeRemoteAuxView(String userId) {
        LogUtil.i(TAG, "removeRemoteAuxView userId:" + userId);
        mHwRtcEngine.stopRemoteSubStreamView(userId);
        mWndArrangeMaker.removeUserVideo("AUX_" + userId);
    }

    public void removeRemoteUser(String userId) {
        LogUtil.i(TAG, "removeRemoteUser userId:" + localUserStates.getmUserId());
        if (isUserPlaying(userId)) {
            removeRtcVideo(userId, false);
            mWndArrangeMaker.removeUserVideo(userId);
        }
        removeRoomMember(userId);
    }

    public void reRenderLocalUser() {
        LogUtil.i(TAG, "reRenderLocalUser userId:" + localUserStates.getmUserId());
        SurfaceView surface = prepareRtcVideo(localUserStates.getmUserId(), true, HRTCEnums.HRTCStreamType.values()[localUserStates.getStreamSelect()]);
        String removeUserId = mWndArrangeMaker.reAddLocalUserVideoSurface(localUserStates.getmUserId(), surface, true);
        if (removeUserId != null) {
            changePlayState(removeUserId);
        }
    }

    public void removeLocalUser() {
        LogUtil.e(TAG, "removeLocalUser userId:" + localUserStates.getmUserId());
        removeRtcVideo(localUserStates.getmUserId(), true);
        mWndArrangeMaker.removeUserVideo(localUserStates.getmUserId());
    }

    public void unSelectRemoteUser(String userId) {
        removeRtcVideo(userId, false);
        mWndArrangeMaker.unSelectUserVideo(userId);
    }

    public void openAudio(String userId) {
        mHwRtcEngine.muteRemoteAudio(userId, false);
        Toast.makeText(this, "opened " + userId + " audio", Toast.LENGTH_SHORT).show();
        changeAudioState(userId);
    }

    public void closeAudio(String userId) {
        mHwRtcEngine.muteRemoteAudio(userId, true);
        changeAudioState(userId);
        Toast.makeText(this, "closed " + userId + " audio", Toast.LENGTH_SHORT).show();
    }

    public void onHangupClicked(View view) {
        LogUtil.i(TAG, "onHangupClicked!");
//
        mLeaveManually = true;
        finish();
        mHwRtcEngine.leaveRoom();
    }

    public void onSwitchCameraClicked(View view) {
        LogUtil.i(TAG, "onSwitchCameraClicked !");
        mHwRtcEngine.switchCamera();
    }


    public void onUploadLog(View view) {
        LogUtil.i(TAG, "onUploadLog !");
        logUploadBtn.setClickable(false);
        handler.sendEmptyMessage(ProgressHandler.START);
        mHwRtcEngine.logUpload();
    }

    public void onChangeRole(View view) {
        LogUtil.i(TAG, "onChangeRole !");
        if (view.isActivated()) {
            mHwRtcEngine.setUserRole(HRTC_ROLE_TYPE_PLAYER);
        } else {
            mHwRtcEngine.setUserRole(HRTC_ROLE_TYPE_JOINER);
        }
    }

    public void onAudioRouteClicked(View view) {
        LogUtil.i(TAG, "onAudioRouteClicked!");
        int result = mHwRtcEngine.setSpeakerModel(view.isActivated() ? HRTC_SPEAKER_MODE_EARPIECE : HRTC_SPEAKER_MODE_SPEAKER);
        if (result == 0) {
            view.setActivated(!view.isActivated());
            view.setEnabled(false);
        }
    }

    private void muteLocalAudio(boolean muted) {
        if (muted) {
            mHwRtcEngine.adjustRecordingVolume(0);
        } else {
            mHwRtcEngine.adjustRecordingVolume(10);
        }
    }

    private void muteLocalVideo(boolean muted) {
        mHwRtcEngine.enableLocalVideo(!muted);  // enable == !muted
    }

    public void onMuteAudioClicked(View view) {
        LogUtil.i(TAG, "onMuteAudioClicked !");
        muteLocalAudio(view.isActivated());
        view.setActivated(!view.isActivated());
    }

    public void onMuteVideoClicked(View view) {
        LogUtil.i(TAG, "onMuteVideoClicked !");
        muteLocalVideo(view.isActivated());
        view.setActivated(!view.isActivated());
    }

    private boolean needToReturnToMain(HRTCEnums.HRTCConnStateTypes state) {
        if (HRTCEnums.HRTCConnStateTypes.HRTC_CONN_FAILED == (state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onError(int s, String s1) {
        final int errcode = s;
        final String msg = s1;
        LogUtil.i(TAG, "onError s:" + s + ", s1:" + s1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(LiveActivity.this, "errcode:" + errcode + System.getProperty("line.separator") + "msg:" + msg, Toast.LENGTH_SHORT);
                setToast(toast);

                if (HRTCConstants.HRTCErrorCode.HRTC_ERR_CODE_JOIN_ROOM_STATUS_BUSY == errcode || errcode == HRTCConstants.HRTCErrorCode.HRTC_ERR_CODE_KICKED_OFF) {
                    LogUtil.i(TAG, "call leave room");
                    mLeaveManually = true;
                    finish();
                    mHwRtcEngine.leaveRoom();
                }
            }
        });
    }

    @Override
    public void onJoinRoomSuccess(String roomId, String userId) {
        LogUtil.i(TAG, "onJoinRoomSuccess roomId:" + roomId + ", userId:" + userId);
    }

    @Override
    public void onLeaveRoom(HRTCEnums.HRTCLeaveReason reason, HRTCStatsInfo statsInfo) {
        LogUtil.i(TAG, "onLeaveRoom, reason:" + reason);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    public void onUserJoined(String roomId, final String userId, String nickname) {
        LogUtil.i(TAG, "onUserJoined roomId:" + roomId + ", userId:" + userId + ", nickname:" + nickname);
        if (userId.equals(localUserStates.getmUserId()) || localUserStates.getmRole() == HRTC_ROLE_TYPE_PUBLISER.ordinal()) {
            return;
        }

        mHwRtcEngine.selectVideoFrameOutput(userId);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (IsUserIsExist(userId)) {
                    LogUtil.i(TAG, "onUserJoined userId:" + userId + " is exsit");
                    return;
                }

                Toast toast = Toast.makeText(LiveActivity.this, userId + " has joined room", Toast.LENGTH_SHORT);
                setToast(toast);

                if (getNumberOfPlaying() >= localUserStates.getmThreshold()) {
                    BeanRoomMember beanRoomMember = new BeanRoomMember(userId, false, false);
                    beanRoomMember.setAudioOPen(!isMuteAll);
                    roommemberBeansList.add(beanRoomMember);//添加房间成员信息
                    if (commentRecyclerAdapter != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.i(TAG, "onUserJoined roommemberBeansList num more than Threshold userId:" + userId);
                                membersTv.setText("(" + roommemberBeansList.size() + ")");
                                commentRecyclerAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    return;
                } else {
                    BeanRoomMember beanRoomMember = new BeanRoomMember(userId, IsHaveAux() ? false : true, false);
                    beanRoomMember.setAudioOPen(!isMuteAll);
                    roommemberBeansList.add(beanRoomMember);//添加房间成员信息
                    if (commentRecyclerAdapter != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                LogUtil.i(TAG, "onUserJoined roommemberBeansList num less than Threshold userId:" + userId);
                                membersTv.setText("(" + roommemberBeansList.size() + ")");
                                commentRecyclerAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }

                if (!IsHaveAux()) {
                    LogUtil.i(TAG, "onUserJoined IsHaveAux NO userId:" + userId);
                    renderRemoteUser(userId, HRTCEnums.HRTCStreamType.values()[localUserStates.getStreamSelect()]);
                }

            }
        });
    }

    @Override
    public void onUserOffline(String roomId, final String userId, int reason) {
        LogUtil.i(TAG, "onUserOffline roomId:" + roomId + ", userId:" + userId + ", reason:" + reason);
        if (userId.equals(localUserStates.getmUserId()) || localUserStates.getmRole() == HRTC_ROLE_TYPE_PUBLISER.ordinal()) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "run removeRemoteUser! ");
                Toast toast = Toast.makeText(LiveActivity.this, userId + " leaves room!", Toast.LENGTH_SHORT);
                setToast(toast);
                removeRemoteUser(userId);

                if (commentRecyclerAdapter != null) {
                    membersTv.setText("(" + roommemberBeansList.size() + ")");
                    commentRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public void onFirstRemoteVideoDecoded(String roomId, final String userId, int width, int height) {
        LogUtil.i(TAG, "onFirstRemoteVideoDecoded userId:" + userId + ", roomId:" + roomId + ", width:" + width + ", height:" + height);
    }
    public void onAgreedStreamAvailable(String roomId, final String userId, final HRTCEnums.HRTCStreamType streamType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(LiveActivity.this, "default stream received: " + userId, Toast.LENGTH_SHORT);
                setToast(toast);
                roommemberBeansList.add(new BeanRoomMember(userId, IsHaveAux() ? false : true, false));//添加房间成员信息
                if (!IsHaveAux()) {
                    renderRemoteUser(userId, streamType);
                }
            }
        });
        LogUtil.i(TAG, "onAgreedStreamAvailable userId:" + userId + ", roomId:" + roomId + ", streamType:" + streamType);
    }

    public int getNumberOfPlaying() {
        int num = 0;
        for (int i = 0; i < roommemberBeansList.size(); i++) {
            if (roommemberBeansList.get(i).getIsPlaying()) {
                num++;
            }
        }
        LogUtil.i(TAG, "getNumberOfPlaying num:" + num);
        return num;
    }

    public void removeRoomMember(String userId) {
        LogUtil.i(TAG, "removeRoomMember userId:" + userId);
        for (int i = 0; i < roommemberBeansList.size(); i++) {
            LogUtil.i(TAG, "roommemberBeansList.get(i): " + roommemberBeansList.get(i) + " userId:" + userId);
            if (roommemberBeansList.get(i).getUserId().equals(userId)) {
                roommemberBeansList.remove(i);
                break;
            }
        }
    }

    public boolean IsUserIsExist(String userId) {
        LogUtil.i(TAG, "IsUserIsExist userId:" + userId);
        for (int i = 0; i < roommemberBeansList.size(); i++) {
            if (roommemberBeansList.get(i).getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    public void changePlayState(String userId) {
        LogUtil.i(TAG, "changePlayState userId:" + userId);
        for (int i = 0; i < roommemberBeansList.size(); i++) {
            if (roommemberBeansList.get(i).getUserId().equals(userId)) {
                if (roommemberBeansList.get(i).getIsPlaying()) {
                    roommemberBeansList.get(i).setIsPlaying(false);
                } else {
                    roommemberBeansList.get(i).setIsPlaying(true);
                }
            }
        }
    }

    public void changeAudioState(String userId) {
        LogUtil.i(TAG, "changeAudioState userId:" + userId);
        for (int i = 0; i < roommemberBeansList.size(); i++) {
            if (roommemberBeansList.get(i).getUserId().equals(userId)) {
                // audio状态取反
                roommemberBeansList.get(i).setAudioOPen(!roommemberBeansList.get(i).getIsAudioOPen());
            }
        }
    }

    public boolean IsUserWithAux(String userId) {
        LogUtil.i(TAG, "IsUserWithAux userId:" + userId);
        for (int i = 0; i < roommemberBeansList.size(); i++) {
            if (roommemberBeansList.get(i).getUserId().equals(userId)) {
                return roommemberBeansList.get(i).isWithAux();
            }
        }
        return false;
    }

    public void changeAuxState(String userId, boolean isWithAux) {
        LogUtil.i(TAG, "changeAuxState userId:" + userId + "isWithAux:" + isWithAux);
        for (int i = 0; i < roommemberBeansList.size(); i++) {
            if (roommemberBeansList.get(i).getUserId().equals(userId)) {
                roommemberBeansList.get(i).setWithAux(isWithAux);
            }
        }
    }

    public boolean IsHaveAux() {
        LogUtil.i(TAG, "IsHaveAux !");
        for (int i = 0; i < roommemberBeansList.size(); i++) {
            if (roommemberBeansList.get(i).isWithAux()) {
                return true;
            }
        }
        return false;
    }

    public String getAuxUserid() {
        LogUtil.i(TAG, "getAuxUserid!");
        for (int i = 0; i < roommemberBeansList.size(); i++) {
            if (roommemberBeansList.get(i).isWithAux()) {
                return roommemberBeansList.get(i).getUserId();
            }
        }
        return "";
    }

    private boolean isUserPlaying(String userId) {
        LogUtil.i(TAG, "isUserPlaying userId:" + userId);
        Iterator it = roommemberBeansList.iterator();
        while (it.hasNext()) {
            BeanRoomMember beanRoomMember = (BeanRoomMember) it.next();
            if (beanRoomMember.getUserId().equals(userId)) {
                return beanRoomMember.getIsPlaying();
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        LogUtil.i(TAG, "onBackPressed !");
        showNormalMoreButtonDialog();
    }

    private void showNormalMoreButtonDialog() {
        AlertDialog.Builder normalMoreButtonDialog = new AlertDialog.Builder(this);
        normalMoreButtonDialog.setTitle("leave room");
        normalMoreButtonDialog.setIcon(R.drawable.logo);
        normalMoreButtonDialog.setMessage("confirm to leave room?");

        //设置按钮
        normalMoreButtonDialog.setPositiveButton("confirm"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mLeaveManually = true;
                        dialog.dismiss();
                        finish();
                        mHwRtcEngine.leaveRoom();

                    }
                });
        normalMoreButtonDialog.setNegativeButton("cancel"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        normalMoreButtonDialog.create().show();
    }

    @Override
    public void onConnectionStateChange(HRTCEnums.HRTCConnStateTypes var1, HRTCEnums.HRTCConnChangeReason var2, String var3) {
        LogUtil.i(TAG, "onConnectionStateChange var1" + var1 + "var2:" + var2 + "var3:" + var3);
        final HRTCEnums.HRTCConnStateTypes state = var1;
        final String msg = var3;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(LiveActivity.this, "msg:" + msg, Toast.LENGTH_SHORT);
                setToast(toast);

                if (needToReturnToMain(state)) {
                    finish();
                }
            }
        });
    }

    @Override
    public void onUserRoleChanged(String roomId, HRTCUserInfo.HRTCRoleType roleType, HRTCUserInfo.HRTCRoleType roleType1) {
        LogUtil.i(TAG, "onUserRoleChanged roleType: " + roleType + "roleType1:" + roleType1);

        localUserStates.setmRole(roleType1.ordinal());
        if (roleType == HRTC_ROLE_TYPE_JOINER && roleType1 == HRTC_ROLE_TYPE_PLAYER) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    removeLocalUser();

                    localUserStates.setmThreshold(4);
                    mMuteRoleChangeBtn.setActivated(false);
                    mMuteAudioBtn.setEnabled(false);
                    mMuteVideoBtn.setEnabled(false);
                    mSwitchCameraBtn.setEnabled(false);
                    mSwitchCameraBtn.setActivated(false);
                }
            });

        } else if (roleType == HRTC_ROLE_TYPE_PLAYER && roleType1 == HRTC_ROLE_TYPE_JOINER) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    reRenderLocalUser();

                    localUserStates.setmThreshold(3);
                    mMuteRoleChangeBtn.setActivated(true);
                    mMuteAudioBtn.setEnabled(true);
                    mMuteVideoBtn.setEnabled(true);
                    mSwitchCameraBtn.setEnabled(true);
                    mSwitchCameraBtn.setActivated(true);
                }
            });
        }
    }

    @Override
    public void onAudioRouteChanged(HRTCEnums.HRTCAudioRoute audioRoute) {
        LogUtil.i(TAG, "onAudioRouteChanged: " + audioRoute);
        if (audioRoute == HRTC_AUDIO_ROUTE_SPEAKER) {
            mAudioRouteBtn.setActivated(true);
        } else if (audioRoute == HRTC_AUDIO_ROUTE_HEADSET || audioRoute == HRTC_AUDIO_ROUTE_RECEIVER || audioRoute == HRTC_AUDIO_ROUTE_BLUETOOTH) {
            mAudioRouteBtn.setActivated(false);
        }
        mAudioRouteBtn.setEnabled(true);
    }

    @Override
    public void onRenderVideoFrame(String s, String s1, byte[] bytes, int i, int i1, int i2, int i3) {
        LogUtil.i(TAG, "onRenderVideoFrame demo " + "   " + bytes[0] + "   " + bytes[1] + "   " + bytes[2] + "   " + s + "   " + s1 + "   " + i + "   " + i1 + "   " + i2 + "   " + i3);
        try {
            String fileName = "/testyuv_" + s1 + "_" + i1 + "_" + i2 + ".yuv";
            writeFile(String.valueOf(Environment.getExternalStorageDirectory()) + "/rtcout", fileName, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void writeFile(String path, String fileName, byte[] content)
            throws IOException {
        try {
            File f = new File(path);
            if (!f.exists()) {
                f.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + fileName, true);
            fos.write(content);
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPlaybackAudioFrame(String s, byte[] bytes, int i, int i1, int i2, int i3, int i4) {
        LogUtil.i(TAG, "onPlaybackAudioFrame demo " + "   " + bytes[0] + "   " + bytes[1] + "   " + bytes[2] + "   " + s + "   " + i + "   " + i1 + "   " + i2 + "   " + i3 + "   " + i4);
        try {
            writeFile(String.valueOf(Environment.getExternalStorageDirectory()) + "/rtcout", "/testpcm.pcm", bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public void onVideoStats(List<HRTCLocalVideoStats> localStats, List<HRTCRemoteVideoStats> remoteStats) {

    }

    @SuppressLint("ResourceType")
    @Override
    public void onAudioStats(List<HRTCLocalAudioStats> list, List<HRTCRemoteAudioStats> list1) {

    }

    @Override
    public void onUserSubStreamAvailable(String roomId, final String userId, final boolean available) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (available) {
                    mOrientationEventListener.enable();
                    LogUtil.i(TAG, "onUserSubStreamAvailable add userid: " + userId);
                    Toast toast = Toast.makeText(LiveActivity.this, "default stream received: " + userId, Toast.LENGTH_SHORT);
                    setToast(toast);

                    if (IsHaveAux())
                        return;

                    if (IsUserIsExist(userId)) {
                        changeAuxState(userId, true);
                    } else {
                        roommemberBeansList.add(new BeanRoomMember(userId, false, true));
                    }

                    mMuteRoleChangeBtn.setEnabled(false);
                    removeLocalUser();
                    unSelectAllRemoteUsers();
                    renderRemoteAuxView(userId);
                } else {
                    if (!IsUserWithAux(userId))
                        return;
                    mOrientationEventListener.disable();
                    LogUtil.i(TAG, "onUserSubStreamAvailable remove userid: " + userId);
                    Toast toast = Toast.makeText(LiveActivity.this, "AUX_" + userId + " leaves room!", Toast.LENGTH_SHORT);
                    setToast(toast);

                    mMuteRoleChangeBtn.setEnabled(true);
                    changeAuxState(userId, false);
                    removeRemoteAuxView(userId);
                    if (localUserStates.getmRole() == HRTC_ROLE_TYPE_JOINER.ordinal()) {
                        reRenderLocalUser();
                    }
                    reRenderAllRemoteUsers();
                }
                if (commentRecyclerAdapter != null) {
                    membersTv.setText("(" + roommemberBeansList.size() + ")");
                    commentRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onSubStreamStats(List<HRTCLocalVideoStats> localStats, List<HRTCRemoteVideoStats> remoteStats) {

    }

    @Override
    public void onLogUploadResult(int i) {
        LogUtil.i(TAG, "onLogUploadResult" + i);
        if (i == 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LiveActivity.this, "log upload success", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LiveActivity.this, "log upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        logUploadBtn.setClickable(true);
    }

    @Override
    public void onLogUploadProgress(int i) {
        LogUtil.i(TAG, "onLogUploadProgress" + i);
        Message message = new Message();
        message.arg1 = i;
        message.what = LiveActivity.ProgressHandler.PROGRESS;
        handler.sendMessage(message);
    }

    private LiveActivity.ProgressHandler handler = new LiveActivity.ProgressHandler(this);

    public static class ProgressHandler extends Handler {
        public static final int TOAST = 0X123;

        public static final int START = 0X456;

        public static final int PROGRESS = 0x789;

        WeakReference<LiveActivity> activityWeakReference;

        public ProgressHandler(LiveActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LogUtil.i(TAG, "handleMessage" + msg);
            super.handleMessage(msg);
            UploadDialog uploadDialog = activityWeakReference.get().uploadDialog;
            switch (msg.what) {
                case TOAST:
                    uploadDialog.dismiss();
                    break;
                case START:
                    if (!activityWeakReference.get().isFinishing()) {
                        uploadDialog.show();
                        uploadDialog.setProgressBar(1);
//                        activityWeakReference.get().logExportBtn.setClickable(false);
                    } else {
                    }
//                    activityWeakReference.get().logExportBtn.setBackground(activityWeakReference.get().getResources().getDrawable(R.drawable.btn_upload_unclickable_bg));

                    break;
                case PROGRESS:
                    if (msg.arg1 < 100) {
//                        uploadDialog.show();
                        uploadDialog.setProgressBar(msg.arg1);
                    } else {
//                        activityWeakReference.get().logExportBtn.setClickable(true);
//                        activityWeakReference.get().logExportBtn.setBackground(activityWeakReference.get().getResources().getDrawable(R.drawable.btn_upload_bg));
                        uploadDialog.dismiss();
                    }
                default:
                    break;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        LogUtil.i(TAG, "onResume!");
        if (true == mOnPauseCalled) {
            if (mMuteAudioBtn.isActivated()) {
                muteLocalAudio(false);
            }

            if (mMuteVideoBtn.isActivated()) {
                muteLocalVideo(false);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.i(TAG, "onLogUploadResult!");
        if (false == mLeaveManually) {
            if (mMuteAudioBtn.isActivated()) {
                muteLocalAudio(true);
            }
            if (mMuteVideoBtn.isActivated()) {
                muteLocalVideo(true);
            }
        }
        mOnPauseCalled = true;
    }

    private void setToast(Toast toast) {
        // 这里给了一个1/7屏幕高度的y轴偏移量
        toast.setGravity(Gravity.BOTTOM, 0, toastBottomMargin);
        toast.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i(TAG, "onLogUploadResult!");
        ((RtcApplication) getApplication()).removeEventHandler(this);
    }
}
