package com.lumos.smartdevice.utils.tinytaskonebyone;

import com.lumos.smartdevice.devicectrl.TagInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ddnosh
 * @website http://blog.csdn.net/ddnosh
 */
public abstract class BaseSyncTask implements SyncTask {

    private int id;

    private boolean isComplete=false;
    private boolean isSuccess=false;

    private Map<String, TagInfo> tagInfos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public Map<String, TagInfo> getTagInfos() {
        return tagInfos;
    }

    public void setTagInfos(Map<String, TagInfo> tagInfos) {
        this.tagInfos = tagInfos;
    }

    public void  setResult(boolean isSuccess) {

        this.isComplete = true;
        this.isSuccess=isSuccess;
        TinySyncExecutor.getInstance().finish();
    }

}
