package com.lumos.smartdevice.api;

import com.lumos.smartdevice.api.rop.RopBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturnCloseAction;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturnCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturnOpenAction;
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

        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(Config.URL.own_LoginByAccount, rop, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onSuccess(response);
            }

            @Override
            public void onFailure(String msg, Exception e) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onFailure(msg,e);
            }
        });


    }

    @Override
    public void ownLogout(RopOwnLogout rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(Config.URL.own_Logout, rop, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onSuccess(response);
            }

            @Override
            public void onFailure(String msg, Exception e) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onFailure(msg,e);
            }
        });

    }

    @Override
    public void ownGetInfo(RopOwnGetInfo rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(Config.URL.own_GetInfo, rop, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onSuccess(response);
            }

            @Override
            public void onFailure(String msg, Exception e) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onFailure(msg,e);
            }
        });

    }

    @Override
    public void ownSaveInfo(RopOwnSaveInfo rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(Config.URL.own_SaveInfo, rop, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onSuccess(response);
            }

            @Override
            public void onFailure(String msg, Exception e) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onFailure(msg,e);
            }
        });

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
    public void lockerGetCabinet(RopLockerGetCabinet rop, final ReqHandler reqHandler) {

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

    @Override
    public void lockerSaveBoxOpenResult(RopLockerSaveBoxOpenResult rop, final ReqHandler reqHandler) {

    }

    @Override
    public void identityInfo(RopIdentityInfo rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(Config.URL.identity_Info, rop, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onSuccess(response);
            }

            @Override
            public void onFailure(String msg, Exception e) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onFailure(msg,e);
            }
        });

    }

    @Override
    public void bookerBorrowReturnCreateFlow(RopBookerBorrowReturnCreateFlow rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(Config.URL.booker_BorrowReturn_CreateFlow, rop, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onSuccess(response);
            }

            @Override
            public void onFailure(String msg, Exception e) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onFailure(msg,e);
            }
        });

    }

    @Override
    public void bookerBorrowReturnOpenAction(RopBookerBorrowReturnOpenAction rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(Config.URL.booker_BorrowReturn_OpenAction, rop, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onSuccess(response);
            }

            @Override
            public void onFailure(String msg, Exception e) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onFailure(msg,e);
            }
        });

    }


    @Override
    public void bookerBorrowReturnCloseAction(RopBookerBorrowReturnCloseAction rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(Config.URL.booker_BorrowReturn_CloseAction, rop, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onSuccess(response);
            }

            @Override
            public void onFailure(String msg, Exception e) {
                reqHandler.handleAfterSendMessage();
                reqHandler.onFailure(msg,e);
            }
        });

    }

    @Override
    public void bookerBorrowReturn(RopBookerBorrowReturn rop, final ReqHandler reqHandler) {

        if(reqHandler!=null) {
            reqHandler.sendBeforeSendMessage();
        }

        HttpClient.myPost(Config.URL.booker_BorrowReturn, rop, new HttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                if(reqHandler!=null) {
                    reqHandler.handleAfterSendMessage();
                    reqHandler.onSuccess(response);
                }
            }

            @Override
            public void onFailure(String msg, Exception e) {
                if(reqHandler!=null) {
                    reqHandler.handleAfterSendMessage();
                    reqHandler.onFailure(msg, e);
                }
            }
        });

    }

}
