package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.model.UserBean;

import java.io.Serializable;
import java.util.List;

public class RetUserGetList implements Serializable {
    private int pageSize;
    private int total;

    private List<UserBean> items;


    public List<UserBean> getItems() {
        return items;
    }

    public void setItems(List<UserBean> items) {
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
