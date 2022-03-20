package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.model.BookerBookBean;

import java.io.Serializable;
import java.util.List;

public class RetBookerBorrowReturn implements Serializable {
    private String flowId;
    private List<BookerBookBean> borrowBooks;
    private List<BookerBookBean> returnBooks;

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public List<BookerBookBean> getBorrowBooks() {
        return borrowBooks;
    }

    public void setBorrowBooks(List<BookerBookBean> borrowBooks) {
        this.borrowBooks = borrowBooks;
    }

    public List<BookerBookBean> getReturnBooks() {
        return returnBooks;
    }

    public void setReturnBooks(List<BookerBookBean> returnBooks) {
        this.returnBooks = returnBooks;
    }
}