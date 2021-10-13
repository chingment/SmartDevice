package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.model.LockerBoxUseRecordBean;
import com.lumos.smartdevice.model.UserBean;

import java.io.Serializable;
import java.util.List;

public class RetLockerGetBoxUseRecords implements Serializable {
    private int pageSize;
    private int total;

    private List<LockerBoxUseRecordBean> items;


    public List<LockerBoxUseRecordBean> getItems() {
        return items;
    }

    public void setItems(List<LockerBoxUseRecordBean> items) {
        this.items = items;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
