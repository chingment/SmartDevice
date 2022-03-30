package com.lumos.smartdevice.activity.booker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturn;
import com.lumos.smartdevice.api.vo.BookerDriveLockeqVo;
import com.lumos.smartdevice.api.vo.BookerDriveRfeqVo;
import com.lumos.smartdevice.api.vo.BookerSlotDrivesVo;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.DriveVo;
import com.lumos.smartdevice.devicectrl.ILockeqCtrl;
import com.lumos.smartdevice.devicectrl.IRfeqCtrl;
import com.lumos.smartdevice.devicectrl.LockeqCtrlInterface;
import com.lumos.smartdevice.devicectrl.RfeqCtrlInterface;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BookerCtrlService extends Service {

    private static final String TAG = "BookerCtrlService";

    private CtrlBinder mCtrlBinder = null;

    private BookerCtrl bookerCtrl;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate...");

        bookerCtrl = BookerCtrl.getInstance();


        bookerCtrl.setHandlerListener(new BookerCtrl.OnHandlerListener() {
            @Override
            public void onBorrowReturn(BorrowReturnFlowResultVo result) {
                LogUtil.d(TAG, "actionCode:" + result.getActionCode() + ",actionRemark:" + result.getActionRemark());
                sendBroadcastMsg(result);
            }
        });


        mCtrlBinder = new CtrlBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d(TAG, "onStartCommand...");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.d(TAG, "onBind...");
        return mCtrlBinder;
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy...");

        super.onDestroy();
    }


    private void sendBroadcastMsg(BorrowReturnFlowResultVo result) {

        Intent intent = new Intent();

        intent.setAction("action.booker.borrow.return.flow.result");

        Bundle bundle = new Bundle();
        bundle.putSerializable("result", result);
        intent.putExtras(bundle);

        sendBroadcast(intent);

    }

    public class CtrlBinder extends Binder {
        public void borrowReturnStart(String clientUserId,int identityType,String identityId,DeviceVo device, BookerSlotVo slot) {
            bookerCtrl.borrowReturnStart(clientUserId, identityType, identityId, device, slot);
        }
    }
}
