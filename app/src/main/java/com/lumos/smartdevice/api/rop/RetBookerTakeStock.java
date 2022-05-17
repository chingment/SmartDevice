package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.api.vo.BookerBookVo;

import java.io.Serializable;
import java.util.List;

public class RetBookerTakeStock implements Serializable {
    private String flowId;
    private List<BookerBookVo> books;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public List<BookerBookVo> getBooks() {
        return books;
    }

    public void setBooks(List<BookerBookVo> books) {
        this.books = books;
    }
}
