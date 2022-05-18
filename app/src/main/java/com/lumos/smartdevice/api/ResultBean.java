package com.lumos.smartdevice.api;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.ParameterizedType;


public class ResultBean<T>  {

    private int code;
    private String msg;
    private T data;

    public  ResultBean(){

    }

    public ResultBean(int code , String msg){

        this.code=code;
        this.msg=msg;
    }

    public ResultBean(int code , String msg, T data){
        this.code=code;
        this.msg=msg;
        this.data=data;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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
