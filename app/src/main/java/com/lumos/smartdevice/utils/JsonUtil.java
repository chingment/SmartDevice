package com.lumos.smartdevice.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;


public class JsonUtil<T>  {

    public static <T> T toObject(String json, TypeReference<?> typeReference) {
        try {

            T ob = JSON.parseObject(json, (Type) typeReference);

            if (ob == null)
                return null;

            return (T) ob;

        } catch (Exception ex) {
            return null;
        }
    }
}
