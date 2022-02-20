package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RopBookerBorrowReturnCreateFlow;
import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopIdentityInfo;
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

    void deviceInitData(RopDeviceInitData rop, final ReqHandler reqHandler);
    void ownLoginByAccount(RopOwnLoginByAccount rop, final ReqHandler reqHandler);
    void ownLogout(RopOwnLogout rop, final ReqHandler reqHandler);
    void ownGetInfo(RopOwnGetInfo rop, final ReqHandler reqHandler);
    void ownSaveInfo(RopOwnSaveInfo rop, final ReqHandler reqHandler);
    void userSave(RopUserSave rop, final ReqHandler reqHandler);
    void userGetList(RopUserGetList rop, final ReqHandler reqHandler);
    void userGetDetail(RopUserGetDetail rop, final ReqHandler reqHandler);
    void lockerGetCabinet(RopLockerGetCabinet rop, final ReqHandler reqHandler);
    void lockerGetBox(RopLockerGetBox rop, final ReqHandler reqHandler);
    void lockerSaveBoxUsage(RopLockerSaveBoxUsage rop, final ReqHandler reqHandler);
    void lockerDeleteBoxUsage(RopLockerDeleteBoxUsage rop, final ReqHandler reqHandler);
    void lockerGetBoxUseRecords(RopLockerGetBoxUseRecords rop, final ReqHandler reqHandler);
    void lockerSaveBoxOpenResult(RopLockerSaveBoxOpenResult rop, final ReqHandler reqHandler);
    void identityInfo(RopIdentityInfo rop, final ReqHandler reqHandler);
    void bookerBorrowReturnCreateFlow(RopBookerBorrowReturnCreateFlow rop, final ReqHandler reqHandler);
}
