package com.lumos.smartdevice.api;

import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.api.rop.RetBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RetBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerBorrowReturn;
import com.lumos.smartdevice.api.rop.RopBookerCreateFlow;
import com.lumos.smartdevice.api.rop.RopBookerDisplayBooks;
import com.lumos.smartdevice.api.rop.RopBookerRenewBooks;
import com.lumos.smartdevice.api.rop.RopBookerSawBorrowBooks;
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
import com.lumos.smartdevice.http.HttpClient;
import com.lumos.smartdevice.http.HttpResponseHandler;
import com.lumos.smartdevice.app.Config;
import com.lumos.smartdevice.utils.JsonUtil;
import com.lumos.smartdevice.utils.StringUtil;


public class ReqNetWord implements IReqVersion{

    @Override
    public void deviceInitData(RopDeviceInitData rop, final ReqHandler reqHandler) {


        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(ReqUrl.device_InitData, rop, new HttpResponseHandler() {
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

        HttpClient.myPost(ReqUrl.own_LoginByAccount, rop, new HttpResponseHandler() {
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

        HttpClient.myPost(ReqUrl.own_Logout, rop, new HttpResponseHandler() {
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

        HttpClient.myPost(ReqUrl.own_GetInfo, rop, new HttpResponseHandler() {
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

        HttpClient.myPost(ReqUrl.own_SaveInfo, rop, new HttpResponseHandler() {
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

        HttpClient.myPost(ReqUrl.identity_Info, rop, new HttpResponseHandler() {
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
    public void identityVerify(RopIdentityVerify rop, final ReqHandler reqHandler) {

        reqHandler.sendBeforeSendMessage();

        HttpClient.myPost(ReqUrl.identity_Verify, rop, new HttpResponseHandler() {
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
    public ResultBean<RetBookerCreateFlow> bookerCreateFlow(RopBookerCreateFlow rop) {

        ResultBean<RetBookerCreateFlow> result;
        String response = HttpClient.myPost(ReqUrl.booker_CreateFlow, rop);
        if(StringUtil.isEmpty(response)){
            return  new ResultBean<>(3000,"请求失败");
        }

        result = JsonUtil.toResult(response, new TypeReference<ResultBean<RetBookerCreateFlow>>() {
        });

        return result;
    }

    @Override
    public ResultBean<RetBookerBorrowReturn>  bookerBorrowReturn(RopBookerBorrowReturn rop) {

        ResultBean<RetBookerBorrowReturn> result;
        String response = HttpClient.myPost(ReqUrl.booker_BorrowReturn, rop);
        if(StringUtil.isEmpty(response)){
            return  new ResultBean<>(3000,"请求失败");
        }

        result = JsonUtil.toResult(response, new TypeReference<ResultBean<RetBookerBorrowReturn>>() {
        });

        return result;
    }

    @Override
    public void bookerSawBorrowBooks(RopBookerSawBorrowBooks rop, final ReqHandler reqHandler) {

        if(reqHandler!=null) {
            reqHandler.sendBeforeSendMessage();
        }

        HttpClient.myPost(ReqUrl.booker_SawBorrowBooks, rop, new HttpResponseHandler() {
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

    @Override
    public void bookerRenewBooks(RopBookerRenewBooks rop, final ReqHandler reqHandler) {

        if(reqHandler!=null) {
            reqHandler.sendBeforeSendMessage();
        }

        HttpClient.myPost(ReqUrl.booker_RenewBooks, rop, new HttpResponseHandler() {
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

    @Override
    public void bookerDisplayBooks(RopBookerDisplayBooks rop, final ReqHandler reqHandler) {

        if(reqHandler!=null) {
            reqHandler.sendBeforeSendMessage();
        }

        HttpClient.myPost(ReqUrl.booker_DisplayBooks, rop, new HttpResponseHandler() {
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
