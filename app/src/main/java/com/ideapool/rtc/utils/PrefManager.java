package com.ideapool.rtc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ideapool.rtc.Constants;

public class PrefManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Constants.RTC_PREF_NAME, Context.MODE_PRIVATE);
    }
}
