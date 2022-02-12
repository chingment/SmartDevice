package com.lumos.smartdevice.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.List;

public class CabinetLayoutBean implements Serializable {
    private int spanCount;
    private List<String> Cells;

    public int getSpanCount() {
        return spanCount;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    public List<String> getCells() {
        return Cells;
    }

    public void setCells(List<String> cells) {
        Cells = cells;
    }
}
