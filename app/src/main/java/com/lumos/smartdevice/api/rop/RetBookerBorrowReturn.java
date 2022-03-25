package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.api.vo.BookerBookVo;

import java.io.Serializable;
import java.util.List;

public class RetBookerBorrowReturn implements Serializable {
    private String flowId;
    private List<BookerBookVo> borrowBooks;
    private List<BookerBookVo> returnBooks;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public List<BookerBookVo> getBorrowBooks() {
        return borrowBooks;
    }

    public void setBorrowBooks(List<BookerBookVo> borrowBooks) {
        this.borrowBooks = borrowBooks;
    }

    public List<BookerBookVo> getReturnBooks() {
        return returnBooks;
    }

    public void setReturnBooks(List<BookerBookVo> returnBooks) {
        this.returnBooks = returnBooks;
    }
}
