package com.lumos.smartdevice.api;

public class ResultUtil {

    public static String isFailureJson(String msg){
        return new ResultBean(ResultCode.FAILURE, msg).toJSONString();
    }

}
