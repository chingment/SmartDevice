package com.lumos.smartdevice.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lumos.smartdevice.R;


/**
 * Created by chingment on 2018/3/28.
 */

// public static void showMessage(final Context act, final String msg,final int len)
//final int len) {

public class ToastUtil {


    public static void showMessage(final Context context, final String message,
                                   int HIDE_DELAY) {


        new Thread() {

            public void run() {
                Looper.prepare();

                Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                LinearLayout layout = (LinearLayout) toast.getView();
                TextView tv = (TextView) layout.getChildAt(0);
                //tv.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_14));

                tv.setTextSize(DisplayUtil.px2sp(context,context.getResources().getDimension(R.dimen.sp_14)));

                layout.setBackgroundResource(R.drawable.toast_bg);
                tv.setTextColor(context.getResources().getColor(R.color.white));
                tv.setPadding(25,5,25,5);
                toast.setGravity(Gravity.CENTER, 5, 5);
                toast.show();
                Looper.loop();
            }
        }.start();
    }

}
