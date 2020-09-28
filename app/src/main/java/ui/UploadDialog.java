package com.huawei.rtcdemo.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.huawei.rtcdemo.R;

import java.util.Locale;

import androidx.annotation.NonNull;

/**
 * @author LJ
 * @version 1.0
 * @date 2019-4-30
 * @fileName com.yjsm.host.cloudvideo.moudle.upload.UploadDialog
 */
public class UploadDialog extends Dialog {

    private TextView tvDownloadSuccess;

    private ImageView ivUploadSuccess;

    private ProgressBar progressBar;

    private TextView tvUpload;

    public UploadDialog(@NonNull Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_upload);
        initView();
    }

    private void initView() {
        tvDownloadSuccess = findViewById(R.id.tv_upload_success);
        tvUpload = findViewById(R.id.tv_upload_progress);
        ivUploadSuccess = findViewById(R.id.iv_upload_success);
        progressBar = findViewById(R.id.upload_progress);
    }

    public void setProgressBar(int progress) {
        if (progressBar == null) {
            return;
        }
        progressBar.setProgress(progress);
        tvUpload.setText(String.format(Locale.CHINA, "uploading %d%%", progress));
        if (progress <= 0) {
            tvUpload.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            tvDownloadSuccess.setVisibility(View.GONE);
            ivUploadSuccess.setVisibility(View.GONE);
        } else if (progressBar.getProgress() >= progressBar.getMax()) {
            tvDownloadSuccess.setVisibility(View.VISIBLE);
            ivUploadSuccess.setVisibility(View.VISIBLE);
            tvUpload.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

}
