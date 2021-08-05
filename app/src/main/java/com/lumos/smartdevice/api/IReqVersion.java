package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopLockerBoxDeleteBelongUser;
import com.lumos.smartdevice.api.rop.RopLockerBoxSaveBelongUser;
import com.lumos.smartdevice.api.rop.RopLockerBoxGetBelongUser;
import com.lumos.smartdevice.api.rop.RopOwnLoginByAccount;
import com.lumos.smartdevice.api.rop.RopOwnLogout;
import com.lumos.smartdevice.api.rop.RopUserGetList;
import com.lumos.smartdevice.api.rop.RopUserSave;

public interface IReqVersion {

    void deviceInitData(RopDeviceInitData rop, final ReqHandler reqHandler);
    void ownLoginByAccount(RopOwnLoginByAccount rop, final ReqHandler reqHandler);
    void ownLogout(RopOwnLogout rop, final ReqHandler reqHandler);
    void userSave(RopUserSave rop, final ReqHandler reqHandler);
    void userGetList(RopUserGetList rop, final ReqHandler reqHandler);
    void lockerBoxSaveBelongUser(RopLockerBoxSaveBelongUser rop, final ReqHandler reqHandler);
    void lockerBoxGetBelongUser(RopLockerBoxGetBelongUser rop, final ReqHandler reqHandler);
    void lockerBoxDeleteBelongUser(RopLockerBoxDeleteBelongUser rop, final ReqHandler reqHandler);
}
