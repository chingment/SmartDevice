package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.List;

public class CabinetLayoutVo implements Serializable {
    private int spanCount;
    private List<String> cells;
    private RfEqVo rfId;


    public int getSpanCount() {
        return spanCount;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    public List<String> getCells() {
        return cells;
    }

    public void setCells(List<String> cells) {
        this.cells = cells;
    }

    public RfEqVo getRfId() {
        return rfId;
    }

    public void setRfId(RfEqVo rfId) {
        this.rfId = rfId;
    }
}
