package com.lumos.smartdevice.model.api;

import com.lumos.smartdevice.model.UserBean;

import java.io.Serializable;
import java.util.List;

public class UserGetListResultBean implements Serializable {
    private List<UserBean> items;


    public List<UserBean> getItems() {
        return items;
    }

    public void setItems(List<UserBean> items) {
        this.items = items;
    }
}
