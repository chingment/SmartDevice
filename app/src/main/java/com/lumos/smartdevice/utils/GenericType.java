package com.lumos.smartdevice.utils;

public class GenericType<T> {
    public Class<T> class2;

    public GenericType(){

    }

    public GenericType(Class<T> class2){
        this.class2=class2;
    }
}
