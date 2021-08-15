package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopLockerBoxDeleteUsage;
import com.lumos.smartdevice.api.rop.RopLockerBoxSaveUsage;
import com.lumos.smartdevice.api.rop.RopLockerBoxGetUsageByUser;
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
    void lockerBoxSaveUsage(RopLockerBoxSaveUsage rop, final ReqHandler reqHandler);
    void lockerBoxGetUsageByUser(RopLockerBoxGetUsageByUser rop, final ReqHandler reqHandler);
    void lockerBoxDeleteUsage(RopLockerBoxDeleteUsage rop, final ReqHandler reqHandler);
}
