package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopLockerDeleteBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBoxs;
import com.lumos.smartdevice.api.rop.RopLockerSaveBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBoxUsages;
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

    void lockerGetBoxs(RopLockerGetBoxs rop, final ReqHandler reqHandler);
    void lockerSaveBoxUsage(RopLockerSaveBoxUsage rop, final ReqHandler reqHandler);
    void lockerGetBoxUsages(RopLockerGetBoxUsages rop, final ReqHandler reqHandler);
    void lockerDeleteBoxUsage(RopLockerDeleteBoxUsage rop, final ReqHandler reqHandler);
}
