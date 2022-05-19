package com.lumos.smartdevice.activity.booker.service;

import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.DeviceVo;

public class BookerCtrl {

    private static final String TAG = "BookerCtrl";

    private static BookerCtrl mThis= null;

    private BookerCtrl(){

    }

    public static BookerCtrl getInstance() {

        if (mThis == null) {
            synchronized (BookerCtrl.class) {
                if (mThis == null) {
                    mThis = new BookerCtrl();
                }
            }
        }

        return mThis;
    }

    public void borrowReturnStart(String flowUserId,int identityType,String identityId,DeviceVo device, BookerSlotVo slot,BorrowReturnFlowThread.OnHandlerListener onHandlerListener) {
        BorrowReturnFlowThread thread = new BorrowReturnFlowThread(flowUserId, identityType, identityId, device, slot, onHandlerListener);
        thread.start();
    }

    public void takeStockStart(String flowUserId, int identityType, String identityId,DeviceVo device, BookerSlotVo slot, TakeStockFlowThread.OnHandlerListener onHandlerListener) {
        TakeStockFlowThread thread = new TakeStockFlowThread(2,flowUserId,identityType,identityId,device, slot,  onHandlerListener);
        thread.start();
    }

    public void openDoorStart(String flowUserId, int identityType, String identityId,DeviceVo device, BookerSlotVo slot, OpenDoorFlowThread.OnHandlerListener onHandlerListener) {
        OpenDoorFlowThread thread = new OpenDoorFlowThread(3,flowUserId,identityType,identityId,device, slot,  onHandlerListener);
        thread.start();
    }

    public boolean checkBorrowReturnIsRunning(BookerSlotVo slot) {
        return BorrowReturnFlowThread.checkRunning(slot);
    }

    public boolean checkTakeStockIsRunning(BookerSlotVo slot) {
        return TakeStockFlowThread.checkRunning(slot);
    }

}
