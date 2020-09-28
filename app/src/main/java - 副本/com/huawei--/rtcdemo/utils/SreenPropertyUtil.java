package com.huawei.rtcdemo.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

public class SreenPropertyUtil {
    private static final String TAG = "SereenPropertyUtil";
    //static int height_dp;
    //static int width_dp;
    static int height_xp;
    //static int width_xp;

    public static void getAndroiodScreenProperty(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        //int width = dm.widthPixels;// 屏幕宽度（像素）
        int height= dm.heightPixels; // 屏幕高度（像素）
        //float density = dm.density;//屏幕密度（0.75 / 1.0 / 1.5）
        //int densityDpi = dm.densityDpi;//屏幕密度dpi（120 / 160 / 240）
        //width_xp =  width;//屏幕宽度(xp)
        height_xp = height;//屏幕高度(xp)
        //屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        //width_dp = (int) (width/density);//屏幕宽度(dp)
        //height_dp = (int)(height/density);//屏幕高度(dp)
    }

    //根据百分比获得PX
    public static int getHeight_xp(float height_percent) {
        return Math.round(height_xp * height_percent);
    }
}
