package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.model.LockerBoxBean;

import java.io.Serializable;
import java.util.HashMap;

public class RetLockerGetBoxs implements Serializable {

   private HashMap<String, LockerBoxBean> boxs;

    public HashMap<String, LockerBoxBean> getBoxs() {
        return boxs;
    }

    public void setBoxs(HashMap<String, LockerBoxBean> boxs) {
        this.boxs = boxs;
    }
}
