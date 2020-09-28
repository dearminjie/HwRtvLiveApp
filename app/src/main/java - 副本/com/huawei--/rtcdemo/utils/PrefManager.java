package com.huawei.rtcdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.huawei.rtcdemo.Constants;

public class PrefManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Constants.RTC_PREF_NAME, Context.MODE_PRIVATE);
    }
}
