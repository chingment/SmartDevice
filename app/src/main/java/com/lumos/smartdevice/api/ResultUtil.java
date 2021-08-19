package com.lumos.smartdevice.api;

public class ResultUtil {

    public static String isFailureJson(String msg){
        return new ResultBean(ResultCode.FAILURE, msg).toJSONString();
    }

    public static ResultBean isSuccess(String msg){
        return new ResultBean(ResultCode.SUCCESS, msg);
    }

    public static ResultBean isFailure(String msg){
        return new ResultBean(ResultCode.FAILURE, msg);
    }

    public static ResultBean isException(String msg){
        return new ResultBean(ResultCode.EXCEPTION, msg);
    }

}
