package com.lumos.smartdevice.api;

import com.alibaba.fastjson.JSON;

public class ResultBean<T>  {

    private int code;
    private String message;
    private T data;

    public  ResultBean(){

    }

    public ResultBean(int code , String message){

        this.code=code;
        this.message=message;
    }

    public ResultBean(int code , String message, T data){

        this.code=code;
        this.message=message;
        this.data=data;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public  String toJSONString() {
        return JSON.toJSONString(this);
    }
}
