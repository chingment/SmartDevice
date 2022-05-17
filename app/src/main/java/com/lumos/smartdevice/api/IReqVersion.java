package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RetBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RetBookerTakeStock;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RopBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerDisplayBooks;
import com.lumos.smartdevice.api.rop.RopBookerRenewBooks;
import com.lumos.smartdevice.api.rop.RopBookerSawBorrowBooks;
import com.lumos.smartdevice.api.rop.RopBookerStockInbound;
import com.lumos.smartdevice.api.rop.RopBookerStockSlots;
import com.lumos.smartdevice.api.rop.RopBookerTakeStock;
import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopIdentityInfo;
import com.lumos.smartdevice.api.rop.RopIdentityVerify;
import com.lumos.smartdevice.api.rop.RopLockerDeleteBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBoxUseRecords;
import com.lumos.smartdevice.api.rop.RopLockerGetCabinet;
import com.lumos.smartdevice.api.rop.RopLockerSaveBoxOpenResult;
import com.lumos.smartdevice.api.rop.RopLockerSaveBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBox;
import com.lumos.smartdevice.api.rop.RopOwnGetInfo;
import com.lumos.smartdevice.api.rop.RopOwnLoginByAccount;
import com.lumos.smartdevice.api.rop.RopOwnLogout;
import com.lumos.smartdevice.api.rop.RopOwnSaveInfo;
import com.lumos.smartdevice.api.rop.RopUserGetDetail;
import com.lumos.smartdevice.api.rop.RopUserGetList;
import com.lumos.smartdevice.api.rop.RopUserSave;

public interface IReqVersion {

    void deviceInitData(RopDeviceInitData rop, ReqHandler reqHandler);

    void ownLoginByAccount(RopOwnLoginByAccount rop, ReqHandler reqHandler);

    void ownLogout(RopOwnLogout rop, ReqHandler reqHandler);

    void ownGetInfo(RopOwnGetInfo rop, ReqHandler reqHandler);

    void ownSaveInfo(RopOwnSaveInfo rop, ReqHandler reqHandler);

    void userSave(RopUserSave rop, ReqHandler reqHandler);

    void userGetList(RopUserGetList rop, ReqHandler reqHandler);

    void userGetDetail(RopUserGetDetail rop, ReqHandler reqHandler);

    void lockerGetCabinet(RopLockerGetCabinet rop, ReqHandler reqHandler);

    void lockerGetBox(RopLockerGetBox rop, ReqHandler reqHandler);

    void lockerSaveBoxUsage(RopLockerSaveBoxUsage rop, ReqHandler reqHandler);

    void lockerDeleteBoxUsage(RopLockerDeleteBoxUsage rop, ReqHandler reqHandler);

    void lockerGetBoxUseRecords(RopLockerGetBoxUseRecords rop, ReqHandler reqHandler);

    void lockerSaveBoxOpenResult(RopLockerSaveBoxOpenResult rop, ReqHandler reqHandler);

    void identityInfo(RopIdentityInfo rop, ReqHandler reqHandler);

    void identityVerify(RopIdentityVerify rop, ReqHandler reqHandler);

    ResultBean<RetBookerCreateFlow> bookerCreateFlow(RopBookerCreateFlow rop);

    ResultBean<RetBookerBorrowReturn> bookerBorrowReturn(RopBookerBorrowReturn rop);

    ResultBean<RetBookerTakeStock> bookerTakeStock(RopBookerTakeStock rop);

    void bookerStockStocks(RopBookerStockSlots rop, ReqHandler reqHandler);

    void bookerStockInbound(RopBookerStockInbound rop, ReqHandler reqHandler);

    void bookerSawBorrowBooks(RopBookerSawBorrowBooks rop, ReqHandler reqHandler);

    void bookerDisplayBooks(RopBookerDisplayBooks rop, ReqHandler reqHandler);

    void bookerRenewBooks(RopBookerRenewBooks rop, ReqHandler reqHandler);
}
