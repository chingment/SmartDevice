package com.lumos.smartdevice.activity.booker.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.api.ReqHandler;
import com.lumos.smartdevice.api.ReqInterface;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RetBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RopBookerCreateFlow;
import com.lumos.smartdevice.api.vo.BookerDriveLockeqVo;
import com.lumos.smartdevice.api.vo.BookerDriveRfeqVo;
import com.lumos.smartdevice.api.vo.BookerSlotVo;
import com.lumos.smartdevice.api.vo.BookerSlotDrivesVo;
import com.lumos.smartdevice.api.vo.DeviceVo;
import com.lumos.smartdevice.api.vo.DriveVo;
import com.lumos.smartdevice.devicectrl.ILockeqCtrl;
import com.lumos.smartdevice.devicectrl.IRfeqCtrl;
import com.lumos.smartdevice.devicectrl.LockeqCtrlInterface;
import com.lumos.smartdevice.devicectrl.RfeqCtrlInterface;
import com.lumos.smartdevice.utils.CommonUtil;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.LogUtil;
import com.lumos.smartdevice.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    public void borrowReturnStart(String clientUserId,int identityType,String identityId,DeviceVo device, BookerSlotVo slot,BorrowReturnFlowThread.OnHandlerListener onHandlerListener) {
        BorrowReturnFlowThread thread = new BorrowReturnFlowThread(clientUserId, identityType, identityId, device, slot, onHandlerListener);
        thread.start();
    }

    public boolean checkBorrowReturnIsRunning(BookerSlotVo slot) {
        return BorrowReturnFlowThread.checkRunning(slot);
    }

}
