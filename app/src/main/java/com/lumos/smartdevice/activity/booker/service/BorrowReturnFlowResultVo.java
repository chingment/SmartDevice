package com.lumos.smartdevice.activity.booker.service;

import java.io.Serializable;
import java.util.HashMap;

public class BorrowReturnFlowResultVo implements Serializable {
    private int actionCode;
    private HashMap<String, Object> actionData;
    private String actionRemark;

    public int getActionCode() {
        return actionCode;
    }

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public HashMap<String, Object> getActionData() {
        return actionData;
    }

    public void setActionData(HashMap<String, Object> actionData) {
        this.actionData = actionData;
    }

    public String getActionRemark() {
        return actionRemark;
    }

    public void setActionRemark(String actionRemark) {
        this.actionRemark = actionRemark;
    }
}
