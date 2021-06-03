package com.fun.chufengpro.util;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.fun.chufengpro.R;

public class setWindowColor {
    public static void setWindowStatusBarColor(Activity activity, int colorResId) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = activity.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

                //顶部状态栏
                window.setStatusBarColor(activity.getResources().getColor(R.color.Home_bar));

                //底部导航栏
                window.setNavigationBarColor(activity.getResources().getColor(R.color.Home_bar));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
