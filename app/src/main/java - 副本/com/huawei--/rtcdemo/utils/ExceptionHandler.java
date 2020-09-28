package com.huawei.rtcdemo.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ExceptionHandler implements UncaughtExceptionHandler
{
    private static final String TAG = "ExceptionHandler";

    private UncaughtExceptionHandler mDefaultHandler = null;

    private Thread main_thread = null;

    // Device properties
    private String brand;

    private String model;

    private String board;

    private String revision;

    private String os_ver;

    public ExceptionHandler(Context ctx)
    {
        brand = android.os.Build.BRAND;
        model = android.os.Build.MODEL;
        board = android.os.Build.BOARD;
        revision = android.os.Build.VERSION.INCREMENTAL;
        os_ver = android.os.Build.VERSION.RELEASE;

        // Store the default exception handler
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        main_thread = ctx.getMainLooper().getThread();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex)
    {
        try
        {
            long uptime = getSystemTime();

            StringBuffer crash_report = new StringBuffer();

            // [DEVICE]
            crash_report.append(getDeviceInfo(uptime));

            // [APPLICATION]
            crash_report.append(getApplicationInfo(uptime));

            // [EXCEPTION]
            crash_report.append("[EXCEPTION]\r\n");

            String name = ex.getMessage();
            if (null != name)
            {
                crash_report.append("Name: ").append(name).append("\r\n");
            }

            crash_report.append("Reason: ").append(ex.toString()).append("\r\n");

            crash_report.append("Process ID: ").append(Process.myPid()).append("\r\n");

            crash_report.append("Thread: ").append(Process.myTid()).append(" (").append(thread.getName()).append(")\r\n");

            StackTraceElement stack_trace[] = ex.getStackTrace();
            if (stack_trace.length > 0)
            {
                for (int i = 1; i <= stack_trace.length; i++)
                {
                    crash_report.append("Call Stack " + i + ": ").append(stack_trace[i - 1].toString()).append("\r\n");
                }
            }

            // Try to find the root cause
            Throwable root_cause = ex;
            while (root_cause.getCause() != null)
            {
                root_cause = root_cause.getCause();
            }

            if (root_cause != ex)
            {
                // We got the root cause which is different from the original exception
                stack_trace = root_cause.getStackTrace();

                if (stack_trace.length > 0)
                {
                    crash_report.append("Caused by: ").append(root_cause.toString()).append("\r\n");

                    for (int i = 1; i <= stack_trace.length; i++)
                    {
                        crash_report.append("Call Stack " + i + ": ").append(stack_trace[i - 1].toString());
                    }
                }
            }
            crash_report.append("\r\n");
            saveToFile(crash_report.toString());
        }
        finally
        {
            if (mDefaultHandler != null)
            {
                Log.i("DmpCrashReporter", "End of crash reporting. System default handler will do the rest work.");
                mDefaultHandler.uncaughtException(thread, ex);
            }
        }
    }

    private String getDeviceInfo(long uptime)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("[DEVICE]\r\n");
        builder.append("Brand: ").append(brand).append("\r\n");
        builder.append("Model: ").append(model).append("\r\n");
        builder.append("Board: ").append(board).append("\r\n");
        builder.append("Revision: ").append(revision).append("\r\n");
        builder.append("Android Version: ").append(os_ver).append("\r\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        String curr_time = sdf.format(Calendar.getInstance().getTime());
        builder.append("System Time :").append(curr_time).append("\r\n");

        builder.append("Boot Up Seconds: ").append(uptime / 1000).append(".").append(uptime % 1000).append("\r\n");
        builder.append("\r\n");
        return builder.toString();
    }

    private String getApplicationInfo(long uptime)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("[APPLICATION]\r\n");
        builder.append("\r\n");

        return builder.toString();
    }

    private long getSystemTime(){
        long time=System.currentTimeMillis();
        return time;
    }

    //保存文件到sd卡，rtcLog目录下，这个目录调用logUpload，整个目录会被上传到后台，用于分析
    public void saveToFile(String content) {
        BufferedWriter out = null;

        Log.i(TAG, "saveToFile: "+content );
        //获取SD卡状态
        String state = Environment.getExternalStorageState();
        //判断SD卡是否就绪
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        String filePath = KLog.getLogPath();
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath + "/demoCrash.txt",true)));
            out.newLine();
            out.write(content);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
