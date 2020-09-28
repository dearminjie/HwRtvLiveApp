package com.huawei.rtcdemo.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huawei.rtc.HRTCEngine;
import com.huawei.rtc.utils.HRTCEnums;
import com.huawei.rtcdemo.Constants;
import com.huawei.rtcdemo.R;
import com.huawei.rtcdemo.RtcApplication;
import com.huawei.rtcdemo.ui.UploadDialog;
import com.huawei.rtcdemo.utils.PrefManager;

import java.lang.ref.WeakReference;

import androidx.appcompat.widget.AppCompatEditText;

import static com.huawei.rtc.utils.HRTCEnums.HRTCStreamType.HRTC_STREAM_TYPE_FHD;
import static com.huawei.rtc.utils.HRTCEnums.HRTCStreamType.HRTC_STREAM_TYPE_HD;

public class SettingsActivity extends BaseActivity {
    private AppCompatEditText mServerEditText;
    private RadioGroup rgStreamSelect;
    private SharedPreferences rtcSp;

    private HRTCEngine mHwRtcEngine;

    private Button logExportBtn;
    private UploadDialog uploadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        rtcSp = PrefManager.getPreferences(getApplicationContext());
        initUI();

        mHwRtcEngine = ((RtcApplication)getApplication()).getEngine();
        ((RtcApplication)getApplication()).registerEventHandler(this);
    }

    private void initUI() {
        mServerEditText = findViewById(R.id.setting_server);
        rgStreamSelect = findViewById(R.id.rg_stream_select);
        String serverAddr = rtcSp.getString(Constants.RTC_PREF_SERVER_ADDR, Constants.RTC_DEFAULT_SERVER_ADDR);

        if(serverAddr != null) {
            mServerEditText.setText(serverAddr);
        }

        int streamType = rtcSp.getInt(Constants.RTC_PREF_STREAM_SELECT, HRTC_STREAM_TYPE_HD.ordinal());
        HRTCEnums.HRTCStreamType st= HRTCEnums.HRTCStreamType.values()[streamType];
        switch (st){
            case HRTC_STREAM_TYPE_HD:
                rgStreamSelect.check(R.id.radio_signal_stream_hd);
                break;
            case HRTC_STREAM_TYPE_FHD:
                rgStreamSelect.check(R.id.radio_signal_stream_fhd);
                break;
            default:
                rgStreamSelect.check(R.id.radio_signal_stream_hd);
                break;
        }

        if (uploadDialog == null) {
            uploadDialog = new UploadDialog(this);
        }

        logExportBtn = findViewById(R.id.live_log_export);
        uploadDialog.setCanceledOnTouchOutside(true);


    }

    private void savePreference(){
        rtcSp.edit().putString(Constants.RTC_PREF_SERVER_ADDR, mServerEditText.getText().toString())
                .apply();

        switch (rgStreamSelect.getCheckedRadioButtonId()){
            case R.id.radio_signal_stream_hd:
                rtcSp.edit().putInt(Constants.RTC_PREF_STREAM_SELECT, HRTC_STREAM_TYPE_HD.ordinal()).apply();
                break;
            case R.id.radio_signal_stream_fhd:
                rtcSp.edit().putInt(Constants.RTC_PREF_STREAM_SELECT, HRTC_STREAM_TYPE_FHD.ordinal()).apply();
                break;
            default:
                rtcSp.edit().putInt(Constants.RTC_PREF_STREAM_SELECT, HRTC_STREAM_TYPE_HD.ordinal()).apply();
                break;
        }
    }
    @Override
    protected void onGlobalLayoutCompleted() {
        // Adjust for status bar height
        RelativeLayout titleLayout = findViewById(R.id.role_title_layout);
        RelativeLayout.LayoutParams params =
                (RelativeLayout.LayoutParams) titleLayout.getLayoutParams();
        params.height += mStatusBarHeight;
        titleLayout.setLayoutParams(params);
    }

    @Override
    public void onBackPressed() {
        onBackArrowPressed(null);
    }

    public void onBackArrowPressed(View view) {
        savePreference();
        finish();
    }

    @Override
    public void onLogUploadResult(int i) {
        if (i == 0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SettingsActivity.this,"log upload success",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SettingsActivity.this,"log upload failed",Toast.LENGTH_SHORT).show();
                }
            });
        }
        logExportBtn.setClickable(true);
    }

    @Override
    public void onLogUploadProgress(int i) {
        Log.d("onLogUploadProgress","onLogUploadProgress"+i );
            Message message = new Message();
            message.arg1 = i;
            message.what = ProgressHandler.PROGRESS;
            handler.sendMessage(message);
    }

    //日志上传点击
    public void onLogExportClicked(View view) {
        Log.d("onLogExportClicked","onLogExportClicked" );
        handler.sendEmptyMessage(ProgressHandler.START);
        mHwRtcEngine.logUpload();
        logExportBtn.setClickable(false);
    }

    private SettingsActivity.ProgressHandler handler = new SettingsActivity.ProgressHandler(this);

    public static class ProgressHandler extends Handler {
        public static final int TOAST = 0X123;

        public static final int START = 0X456;

        public static final int PROGRESS = 0x789;

        WeakReference<SettingsActivity> activityWeakReference;

        public ProgressHandler(SettingsActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }


        @Override
        public void handleMessage(Message msg) {
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
    protected void onDestroy() {
        super.onDestroy();
        ((RtcApplication)getApplication()).removeEventHandler(this);
    }
}
