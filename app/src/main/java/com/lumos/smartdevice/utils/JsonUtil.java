package com.lumos.smartdevice.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.lumos.smartdevice.api.ResultBean;
import com.lumos.smartdevice.api.ResultCode;
import com.lumos.smartdevice.api.rop.RetDeviceCheckAppVersion;
import com.lumos.smartdevice.api.rop.RetDeviceInitData;

import java.lang.reflect.Type;


public class JsonUtil<T>  {

    public static <T> T toObject(String json,TypeReference<T> type) {
        try {

            T ob = JSON.parseObject(json,type);

            if (ob == null)
                return null;

            return (T) ob;

        } catch (Exception ex) {
            return null;
        }
    }


    public static <T> T toResult(String json,TypeReference<T> type) {

        ResultBean<Object> rt=new ResultBean<>();

        try {

            T ob = JSON.parseObject(json, type);

            if (ob == null) {
                rt.setCode(3000);
                rt.setMsg("格式转换错误");
                return (T)rt;
            }

            return (T) ob;

        } catch (Exception ex) {
            rt.setCode(3000);
            rt.setMsg("格式转换错误");
            return (T)rt;
        }

    }


//    public static <T> T toResult(String json) {
//
//        //ResultBean<T> rt=new ResultBean<>();
//
//        try {
//
//           Class c=  default(T);
//
//           Type type= class2.getGenericSuperclass();
//
//            TypeReference<T> t=   new TypeReference<>(){};
//
//             T c_rt = JSON.parseObject(json, type);
//
//           // new T().getClass()
//
//          //  ResultBean<RetDeviceInitData> c_rt = JSONObject.parseObject(json,type);
//
//           // GenericType<T> g_type=new GenericType<>()；
//
//           // T c_rt = JSON.parseObject(json, new TypeReference<T>() {
//           // });
//
//            if(c_rt==null){
//                return  null;
//            }
//
//            return  null;
//
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            //rt.setCode(ResultCode.EXCEPTION);
//            //rt.setMsg("格式转换错误");
//        }
//
//        return  null;
//    }
}
