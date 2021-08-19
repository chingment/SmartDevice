package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.model.LockerBoxUsageBean;

import java.util.List;

public class RetLockerGetBoxUsages {

   private List<LockerBoxUsageBean> usages;

    public List<LockerBoxUsageBean> getUsages() {
        return usages;
    }

    public void setUsages(List<LockerBoxUsageBean> usages) {
        this.usages = usages;
    }
}
