package com.lumos.smartdevice.ostCtrl;

import android.content.Context;

import com.ys.rkapi.MyManager;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OstCtrlByYs implements IOstCtrl {

    private static final String TAG = "OstCtrlByYs";

    public void  reboot(Context context){
        MyManager myManager=MyManager.getInstance(context);
        myManager.reboot();
    }

    public void  shutdown(Context context){
        MyManager myManager=MyManager.getInstance(context);
        myManager.shutdown();
    }

    public void  setHideStatusBar(Context context, boolean ishidden){

        MyManager myManager=MyManager.getInstance(context);
        myManager.hideNavBar(ishidden);
        myManager.setSlideShowNavBar(!ishidden);
        myManager.setSlideShowNotificationBar(!ishidden);
    }

    public void  installApk(Context context, String path){

        MyManager myManager=MyManager.getInstance(context);
        boolean flag= myManager.silentInstallApk(path);


    }

//    private void execLinuxCommand(){
//        String cmd= "sleep 5000; am start -n com.uplink.selfstore/com.uplink.selfstore.activity.InitDataActivity";
//        //Runtime对象
//        Runtime runtime = Runtime.getRuntime();
//        try {
//            Process localProcess = runtime.exec("su");
//            OutputStream localOutputStream = localProcess.getOutputStream();
//            DataOutputStream localDataOutputStream = new DataOutputStream(localOutputStream);
//            localDataOutputStream.writeBytes(cmd);
//            localDataOutputStream.flush();
//
//        } catch (IOException e) {
//
//
//        }
//    }
}
