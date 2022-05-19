package com.lumos.smartdevice.activity.booker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.utils.LogUtil;

public class BookerCtrlService extends Service {

    private static final String TAG = "BookerCtrlService";

    private CtrlBinder mCtrlBinder = null;

    private BookerCtrl bookerCtrl;

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate...");

        bookerCtrl = BookerCtrl.getInstance();

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


    private void sendBroadcastMsg(MessageByAction message) {

        Intent intent = new Intent();

        intent.setAction("action.booker.borrow.return.handle.message");

        Bundle bundle = new Bundle();
        bundle.putSerializable("message", message);
        intent.putExtras(bundle);

        sendBroadcast(intent);

    }

    public class CtrlBinder extends Binder {

        public void borrowReturnStart(String clientUserId, int identityType, String identityId, DeviceVo device, BookerSlotVo slot) {
            bookerCtrl.borrowReturnStart(clientUserId, identityType, identityId, device, slot, new BorrowReturnFlowThread.OnHandlerListener() {
                @Override
                public void handleMessage(MessageByAction message) {
                    LogUtil.d(TAG, "actionCode:" + message.getActionCode() + ",actionRemark:" + message.getActionRemark());
                    sendBroadcastMsg(message);
                }
            });
        }

        public boolean checkBorrowReturnIsRunning(BookerSlotVo slot) {
            return bookerCtrl.checkBorrowReturnIsRunning(slot);
        }

        public void takeStock(String clientUserId, int identityType, String identityId,DeviceVo device, BookerSlotVo slot) {
            bookerCtrl.takeStockStart(clientUserId,identityType,identityId,device, slot, new TakeStockFlowThread.OnHandlerListener() {
                @Override
                public void handleMessage(MessageByAction message) {
                    LogUtil.d(TAG, "actionCode:" + message.getActionCode() + ",actionRemark:" + message.getActionRemark());
                    sendBroadcastMsg(message);
                }
            });
        }

        public boolean checkTakeStockIsRunning(BookerSlotVo slot) {
            return bookerCtrl.checkTakeStockIsRunning(slot);
        }

        public void openDoor(String clientUserId, int identityType, String identityId,DeviceVo device, BookerSlotVo slot) {
            bookerCtrl.openDoorStart(clientUserId,identityType,identityId,device, slot, new OpenDoorFlowThread.OnHandlerListener() {
                @Override
                public void handleMessage(MessageByAction message) {
                    LogUtil.d(TAG, "actionCode:" + message.getActionCode() + ",actionRemark:" + message.getActionRemark());
                    sendBroadcastMsg(message);
                }
            });
        }

    }
}
