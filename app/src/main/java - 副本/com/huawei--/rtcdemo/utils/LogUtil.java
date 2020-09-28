package com.huawei.rtcdemo.utils;


import android.util.Log;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Log使用类
 */
public class LogUtil {

    private static final int VERBOSE = 0;
    private static final int DEBUG = 1;
    private static final int INFO = 2;
    private static final int WARNING = 3;
    private static final int ERROR = 4;
    private static final int NO_LOG = 5;
    private static final String LOG_FILE = KLog.getLogPath()+"/DemoLogInfo.log";

    private static LogUtil mInstance = null;
    private static int LOG_LEVEL = VERBOSE;
    private static boolean logFileEnable = true;

    private static LogUtil getInstance() {
        if (mInstance == null) {
            synchronized (LogUtil.class) {
                if (mInstance == null) {
                    mInstance = new LogUtil();
                }
            }
        }
        return mInstance;
    }

    // verbose
    public static void v(String tag, String msg) {
        if (VERBOSE < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        Log.v(tag, msg);
        write("VERBOSE", getInstance().getPrefixName(), msg);
    }

    public static void v(String msg) {
        if (DEBUG < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        v(getInstance().getPrefixName(), msg);
    }

    // debug
    public static void d(String tag, String msg) {
        if (DEBUG < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        Log.d(tag, msg);
        write("DEBUG", getInstance().getPrefixName(), msg);
    }

    public static void d(String msg) {
        if (DEBUG < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        d(getInstance().getPrefixName(), msg);
    }

    // info
    public static void i(String tag, String msg) {
        if (INFO < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        Log.i(tag, msg);
        write("INFO", getInstance().getPrefixName(), msg);
    }

    public static void i(String msg) {
        if (DEBUG < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        i(getInstance().getPrefixName(), msg);
    }

    // warning
    public static void w(String tag, String msg) {
        if (WARNING < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        Log.w(tag, msg);
        write("WARNING", getInstance().getPrefixName(), msg);
    }

    public static void w(String msg) {
        if (DEBUG < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        w(getInstance().getPrefixName(), msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (WARNING < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        Log.w(tag, msg, tr);
        write("WARNING", getInstance().getPrefixName(), msg);
    }

    // error
    public static void e(String tag, String msg) {
        if (ERROR < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        Log.e(tag, msg);
        write("ERROR", getInstance().getPrefixName(), msg);
    }

    public static void e(String msg) {
        if (DEBUG < LOG_LEVEL)
            return;
        if (msg == null)
            return;
        e(getInstance().getPrefixName(), msg);
    }

    /**
     * 写到文件中的log的前缀，如果因为混淆之类的原因而取不到，就返回"[ minify ]"
     *
     * @return prefix
     */
    private String getPrefixName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();
        if (sts == null || sts.length == 0) {
            return "[ minify ]";
        }
        try {
            for (StackTraceElement st : sts) {
                if (st.isNativeMethod()) {
                    continue;
                }
                if (st.getClassName().equals(Thread.class.getName())) {
                    continue;
                }
                if (st.getClassName().equals(this.getClass().getName())) {
                    continue;
                }
                if (st.getFileName() != null) {
                    return "[ " + Thread.currentThread().getName() +
                            ": " + st.getFileName() + ":" + st.getLineNumber() +
                            " " + st.getMethodName() + " ]";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "[ minify ]";
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param level   等级
     * @param prefix  前缀
     * @param content 内容
     */
    private static void write(String level, String prefix, String content) {
        if (!logFileEnable)
            return;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(LOG_FILE, true);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.getDefault());
            String time = sdf.format(new Date());
            writer.write(time + ": " + level + "/" + prefix + ": " + content + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
