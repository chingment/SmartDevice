package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopLockerBoxDeleteUsage;
import com.lumos.smartdevice.api.rop.RopLockerBoxSaveUsage;
import com.lumos.smartdevice.api.rop.RopLockerBoxGetUsages;
import com.lumos.smartdevice.api.rop.RopOwnLoginByAccount;
import com.lumos.smartdevice.api.rop.RopOwnLogout;
import com.lumos.smartdevice.api.rop.RopUserGetList;
import com.lumos.smartdevice.api.rop.RopUserSave;
import com.lumos.smartdevice.http.HttpClient;
import com.lumos.smartdevice.http.HttpResponseHandler;
import com.lumos.smartdevice.own.Config;


public class ReqNetWord implements IReqVersion{

    @Override
    public void deviceInitData(RopDeviceInitData rop, final ReqHandler reqHandler) {


        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(Config.URL.device_InitData, rop, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                reqHandler.onSuccess(response);
            }

            @Override
            public void onFailure(String msg, Exception e) {
                reqHandler.onFailure(msg,e);
            }
        });

    }

    @Override
    public void ownLoginByAccount(RopOwnLoginByAccount rop, final ReqHandler reqHandler) {


    }

    @Override
    public void ownLogout(RopOwnLogout rop, final ReqHandler reqHandler) {



    }

    @Override
    public void userSave(RopUserSave rop, final ReqHandler reqHandler) {



    }

    @Override
    public void userGetList(RopUserGetList rop, final ReqHandler reqHandler) {



    }

    @Override
    public void lockerBoxSaveUsage(RopLockerBoxSaveUsage rop, final ReqHandler reqHandler) {

    }

    @Override
    public void lockerBoxGetUsages(RopLockerBoxGetUsages rop, final ReqHandler reqHandler) {

    }

    @Override
    public void lockerBoxDeleteUsage(RopLockerBoxDeleteUsage rop, final ReqHandler reqHandler) {

    }
}
