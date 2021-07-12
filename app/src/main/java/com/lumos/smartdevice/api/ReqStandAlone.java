package com.lumos.smartdevice.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lumos.smartdevice.R;
import com.lumos.smartdevice.db.DbManager;

import java.util.Map;

public class ReqStandAlone implements IReqVersion{

    @Override
    public void initDeviceData(Map<String, Object> d, final ReqHandler reqHandler) {

        String username=d.get("username").toString();
        String password=d.get("password").toString();


        if(!DbManager.getInstance().checkUserPassword(username,password,"2")){
            reqHandler.sendFailureMessage("失败",null);
            return;
        }


        reqHandler.sendSuccessMessage("成功");

    }

    @Override
    public void loginByAccount(String username,String password, final ReqHandler reqHandler) {

        ResultBean result=null;

        if(!DbManager.getInstance().checkUserPassword(username,password,"2")){
            result=new ResultBean(ResultCode.FAILURE,"登录失败");
            reqHandler.sendSuccessMessage(result.toJSONString());
            return;
        }

        result=new ResultBean(ResultCode.SUCCESS,"登录成功");

        reqHandler.sendSuccessMessage(result.toJSONString());

    }
}
