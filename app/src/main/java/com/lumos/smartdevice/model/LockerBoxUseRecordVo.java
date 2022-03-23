package com.lumos.smartdevice.model;

import java.io.Serializable;

public class LockerBoxUseRecordVo implements Serializable {

    private String recordId;
    private String cabinetId;
    private String slotId;
    private String useTime;
    private String useAction;
    private int useResult;
    private String useRemark;


    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getCabinetId() {
        return cabinetId;
    }

    public void setCabinetId(String cabinetId) {
        this.cabinetId = cabinetId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getUseAction() {
        return useAction;
    }

    public void setUseAction(String useAction) {
        this.useAction = useAction;
    }

    public int getUseResult() {
        return useResult;
    }

    public void setUseResult(int useResult) {
        this.useResult = useResult;
    }

    public String getUseRemark() {
        return useRemark;
    }

    public void setUseRemark(String useRemark) {
        this.useRemark = useRemark;
    }
}
