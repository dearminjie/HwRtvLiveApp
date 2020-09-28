package com.huawei.rtcdemo.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.rtc.HRTCEngine;
import com.huawei.rtc.models.HRTCStatsInfo;
import com.huawei.rtc.models.HRTCUserInfo;
import com.huawei.rtc.models.HRTCVideoEncParam;
import com.huawei.rtc.utils.HRTCEnums;
import com.huawei.rtcdemo.Constants;
import com.huawei.rtcdemo.R;
import com.huawei.rtcdemo.RtcApplication;
import com.huawei.rtcdemo.utils.ExceptionHandler;
import com.huawei.rtcdemo.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import static com.huawei.rtc.models.HRTCUserInfo.HRTCRoleType.HRTC_ROLE_TYPE_JOINER;
import static com.huawei.rtc.utils.HRTCEnums.HRTCStreamType.HRTC_STREAM_TYPE_FHD;
import static com.huawei.rtc.utils.HRTCEnums.HRTCStreamType.HRTC_STREAM_TYPE_HD;
import static com.huawei.rtc.utils.HRTCEnums.HRTCStreamType.HRTC_STREAM_TYPE_LD;
import static com.huawei.rtc.utils.HRTCEnums.HRTCStreamType.HRTC_STREAM_TYPE_SD;
import static com.huawei.rtc.utils.HRTCConstants.HRTC_SUCCESS;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private EditText mChannelEdit;
    private EditText mUserNameEdit;
    private TextView mStartBtn;
    private boolean mIsChannelIdEt = false;
    private boolean mIsUserNameEt = false;

    private HRTCEngine mHwRtcEngine;
    SurfaceView mPreviewSurface;
    RelativeLayout.LayoutParams mPreviewLayoutParams;
    private ImageView mPreviewIV;

    private RadioGroup rgRole;

    private int mRole = HRTC_ROLE_TYPE_JOINER.ordinal();

    private TextView textView;
    //首先还是先声明这个Spinner控件
    private Spinner spinner;

    //定义一个String类型的List数组作为数据源
    private List<String> dataList;

    //定义一个ArrayAdapter适配器作为spinner的数据适配器
    private ArrayAdapter<String> adapter;

    ArrayList<HRTCVideoEncParam> encParamsList = new ArrayList<HRTCVideoEncParam>();

    private boolean mPermissionGranted = false;

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 200;
    public static final int REQUEST_RECORD_AUDIO_PERMISSION_CODE = 201;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 202;
    public static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE = 203;
    public static final int REQUEST_READ_PHONE_STATE_PERMISSION_CODE = 204;

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS = 1;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2;

    private int toastBottomMargin;//提示框位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            finish();
            return;
        } else {
            checkAllPermission();

            //Android6.0以上需要动态询问获取权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS);
                    requestPermissions(
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                } else {
                    mHwRtcEngine = ((RtcApplication) getApplication()).getEngine();
                }
            }

            ((RtcApplication) getApplication()).registerEventHandler(MainActivity.this);
            setContentView(R.layout.activity_main);

            initUI();

            ExceptionHandler handler = new ExceptionHandler(this);
            Thread.setDefaultUncaughtExceptionHandler(handler);

            // 获取屏幕高度
            toastBottomMargin = getWindowManager().getDefaultDisplay().getHeight() / 7;
        }

    }

    private void initUI() {
        mChannelEdit = findViewById(R.id.et_channel);
        mChannelEdit.addTextChangedListener(mChannelIdETWatcher);

        mUserNameEdit = findViewById(R.id.et_username);
        mUserNameEdit.addTextChangedListener(mUserNameETWatcher);

        mPreviewIV = findViewById(R.id.preview_button);

        textView = (TextView) findViewById(R.id.tv_encode_choose);
        spinner = (Spinner) findViewById(R.id.spinner);

        //为dataList赋值，将下面这些数据添加到数据源中
        dataList = new ArrayList<String>();
        dataList.add("HD");
        dataList.add("FHD");
        dataList.add("HD+FHD");

        /*为spinner定义适配器，也就是将数据源存入adapter，这里需要三个参数
        1. 第一个是Context（当前上下文），这里就是this
        2. 第二个是spinner的布局样式，这里用android系统提供的一个样式
        3. 第三个就是spinner的数据源，这里就是dataList*/
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,dataList);

        //为适配器设置下拉列表下拉时的菜单样式。
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //为spinner绑定我们定义好的数据适配器
        spinner.setAdapter(adapter);

        spinner.setSelection(0);

        //为spinner绑定监听器，这里我们使用匿名内部类的方式实现监听器
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                textView.setText("清晰度:");//+adapter.getItem(position)
                LogUtil.i(TAG, "onItemSelected: "+position+id);
                encParamsList.clear();
                switch (position){
                    case 0:
                        encParamsList.add(new HRTCVideoEncParam(HRTC_STREAM_TYPE_HD));
                        break;
                    case 1:
                        encParamsList.add(new HRTCVideoEncParam(HRTC_STREAM_TYPE_FHD));
                        break;
                    case 2:
                        encParamsList.add(new HRTCVideoEncParam(HRTC_STREAM_TYPE_HD));
                        encParamsList.add(new HRTCVideoEncParam(HRTC_STREAM_TYPE_FHD));
                        break;
                    default:
                        encParamsList.add(new HRTCVideoEncParam(HRTC_STREAM_TYPE_HD));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                textView.setText("请选择编码参数");
            }
        });

        mStartBtn = findViewById(R.id.bt_join_channel);
        if (TextUtils.isEmpty(mChannelEdit.getText()) || TextUtils.isEmpty(mUserNameEdit.getText())) mStartBtn.setEnabled(false);

        rgRole = findViewById(R.id.rg_choose_role);;
        rgRole.setOnCheckedChangeListener(new RoleRadioButtonListener());
    }

    class RoleRadioButtonListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // 选中状态改变时被触发
            switch (checkedId) {
                case R.id.radio_joiner:
                    // 当用户选择参与者加入时
                    mRole = HRTCUserInfo.HRTCRoleType.HRTC_ROLE_TYPE_JOINER.ordinal();
                    break;
                case R.id.radio_palyer:
                    // 当用户选择观众时
                    mRole = HRTCUserInfo.HRTCRoleType.HRTC_ROLE_TYPE_PLAYER.ordinal();
                    break;
                case R.id.radio_publisher:
                    // 当用户选择主播时
                    mRole = HRTCUserInfo.HRTCRoleType.HRTC_ROLE_TYPE_PUBLISER.ordinal();
                    break;
                default :
                    break;
            }
        }

    }

    private TextWatcher mChannelIdETWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Do nothing
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mIsChannelIdEt = !TextUtils.isEmpty(editable);
            mStartBtn.setEnabled(mIsChannelIdEt && mIsUserNameEt);
        }
    };

    private TextWatcher mUserNameETWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Do nothing
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // Do nothing
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mIsUserNameEt = !TextUtils.isEmpty(editable);
            mStartBtn.setEnabled(mIsChannelIdEt && mIsUserNameEt);
        }
    };

    public void onSettingClicked(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void onPreViewClicked(View view) {
        if(!mPermissionGranted){
            checkAllPermission();
            return;
        }

        if (view.isActivated()) {
            mHwRtcEngine.stopPreview();

            if(mPreviewSurface!=null){
                ViewGroup parent = (ViewGroup)mPreviewSurface.getParent();
                if(parent!=null)
                {
                    parent.removeView(mPreviewSurface);
                }
            }
        } else {
            mPreviewLayoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            mPreviewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            mPreviewLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, R.id.preview_button);

            mPreviewLayoutParams.width = 400;
            mPreviewLayoutParams.height=600;

            mPreviewSurface = HRTCEngine.createRenderer(getApplicationContext());
            mHwRtcEngine.setupLocalView(mPreviewSurface, HRTCEnums.HRTCVideoDisplayMode.HRTC_VIDEO_DISPLAY_MODE_HIDDEN);

            addContentView(mPreviewSurface,mPreviewLayoutParams);

            mHwRtcEngine.startPreview();
        }
        view.setActivated(!view.isActivated());
    }


    public void onJoinChannelClicked(View view) {
        jumpLiveActivity();
    }

    private void jumpLiveActivity() {
        if(!mPermissionGranted){
            checkAllPermission();
            return;
        }

        mHwRtcEngine = ((RtcApplication)getApplication()).getEngine();

        /*预览视图去除以及置灰*/
        mPreviewIV.setActivated(false);
        if(mPreviewSurface!=null){
            ViewGroup parent = (ViewGroup)mPreviewSurface.getParent();
            if(parent!=null)
            {
                parent.removeView(mPreviewSurface);
            }
        }

        String channelId = mChannelEdit.getText().toString();
        String username = mUserNameEdit.getText().toString();

        /*加入房间*/
        LogUtil.i(Constants.RTC_TAG, "joinRoom");
        mHwRtcEngine.setVideoEncParam(Integer.valueOf(Constants.DEFAULT_BITRATE),encParamsList);
        HRTCUserInfo userInfo = new HRTCUserInfo();
        userInfo.setUserId(username);
        userInfo.setUserName(username);
        userInfo.setRole(HRTCUserInfo.HRTCRoleType.values()[mRole]);

        //这里调用，节省首帧时间
        ((RtcApplication) getApplication()).clearCachedEvents();  // clear events before join
        int ret = mHwRtcEngine.joinRoom(userInfo, channelId, HRTCEnums.HRTCMediaType.HRTC_MEDIA_TYPE_VIDEO);

        if (HRTC_SUCCESS != ret) {
            Toast toast =Toast.makeText(MainActivity.this,   "join room failed, input violation!", Toast.LENGTH_SHORT);
            setToast(toast);
            mHwRtcEngine.leaveRoom();
            return;
        }

        /*界面跳转*/
        Intent intent = new Intent(getIntent());
        intent.putExtra(Constants.KEY_ROLE, mRole);
        intent.putExtra(Constants.KEY_CHANNELID, channelId);
        intent.putExtra(Constants.KEY_USERID, username);
        intent.setClass(this, LiveActivity.class);
        startActivity(intent);
    }

    private void setToast(Toast toast){
        // 这里给了一个1/7屏幕高度的y轴偏移量
        toast.setGravity(Gravity.BOTTOM, 0, toastBottomMargin);
        toast.show();
    }

    private void checkAllPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)) {
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)) {
                                mPermissionGranted = true;
                            } else {
                                requestPermissions(new String[]{android.Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE_PERMISSION_CODE);
                            }
                        } else {
                            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                        }
                    } else {
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                    }
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION_CODE);
                }
            } else {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
            }
        } else {
            mPermissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED)
            return;

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION_CODE:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO)) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            mPermissionGranted = true;
                        } else
                            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                    } else
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION_CODE);
                }
                break;
            case REQUEST_RECORD_AUDIO_PERMISSION_CODE:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        mPermissionGranted = true;
                    } else
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                } else
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                break;
            case REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION_CODE:
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    mPermissionGranted = true;
                    mHwRtcEngine = ((RtcApplication)getApplication()).getEngine();
                } else
                    requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE);
                break;
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION_CODE:
                mPermissionGranted = true;
                break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
