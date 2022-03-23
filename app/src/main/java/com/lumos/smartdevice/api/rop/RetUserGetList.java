package com.lumos.smartdevice.api.rop;

import com.lumos.smartdevice.model.UserVo;

import java.io.Serializable;
import java.util.List;

public class RetUserGetList implements Serializable {
    private int pageNum;
    private int pageSize;
    private long totalSize;
    private int totalPages;

    private List<UserVo> items;


    public List<UserVo> getItems() {
        return items;
    }

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

    public void setItems(List<UserVo> items) {
        this.items = items;
    }
}
