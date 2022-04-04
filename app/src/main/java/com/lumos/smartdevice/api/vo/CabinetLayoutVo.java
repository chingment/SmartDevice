package com.lumos.smartdevice.api.vo;

import java.io.Serializable;
import java.util.List;

public class CabinetLayoutVo implements Serializable {
    private int spanCount;
    private List<String> cells;

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
}

