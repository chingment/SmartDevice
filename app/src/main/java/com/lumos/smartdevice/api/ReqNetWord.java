package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RopDeviceInitData;
import com.lumos.smartdevice.api.rop.RopLockerDeleteBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBoxUseRecords;
import com.lumos.smartdevice.api.rop.RopLockerGetBoxs;
import com.lumos.smartdevice.api.rop.RopLockerSaveBoxUsage;
import com.lumos.smartdevice.api.rop.RopLockerGetBox;
import com.lumos.smartdevice.api.rop.RopOwnLoginByAccount;
import com.lumos.smartdevice.api.rop.RopOwnLogout;
import com.lumos.smartdevice.api.rop.RopUserGetDetail;
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
    public void userGetDetail(RopUserGetDetail rop, final ReqHandler reqHandler){

    }

    @Override
    public void lockerGetBoxs(RopLockerGetBoxs rop, final ReqHandler reqHandler) {

    }

    @Override
    public void lockerSaveBoxUsage(RopLockerSaveBoxUsage rop, final ReqHandler reqHandler) {

    }

    @Override
    public void lockerGetBox(RopLockerGetBox rop, final ReqHandler reqHandler) {

    }

    @Override
    public void lockerDeleteBoxUsage(RopLockerDeleteBoxUsage rop, final ReqHandler reqHandler) {

    }

    @Override
    public void lockerGetBoxUseRecords(RopLockerGetBoxUseRecords rop, final ReqHandler reqHandler) {

    }
}
