package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.api.vo.BookerSlotVo;

import java.util.List;

public class RetBookerStockSlots {
    private int pageNum;
    private int pageSize;
    private long totalSize;
    private int totalPages;
    private List<BookerSlotVo> items;
    private int stockQuantity;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<BookerSlotVo> getItems() {
        return items;
    }

    public void setItems(List<BookerSlotVo> items) {
        this.items = items;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
