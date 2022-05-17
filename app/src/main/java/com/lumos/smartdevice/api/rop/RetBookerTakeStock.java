package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.api.vo.BookerBookVo;

import java.io.Serializable;
import java.util.List;

public class RetBookerTakeStock implements Serializable {
    private String flowId;
    private String sheetId;
    private List<BookerBookVo> sheetItems;
    private List<BookerBookVo> warnItems;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getSheetId() {
        return sheetId;
    }

    public void setSheetId(String sheetId) {
        this.sheetId = sheetId;
    }

    public List<BookerBookVo> getSheetItems() {
        return sheetItems;
    }

    public void setSheetItems(List<BookerBookVo> sheetItems) {
        this.sheetItems = sheetItems;
    }

    public List<BookerBookVo> getWarnItems() {
        return warnItems;
    }

    public void setWarnItems(List<BookerBookVo> warnItems) {
        this.warnItems = warnItems;
    }
}
