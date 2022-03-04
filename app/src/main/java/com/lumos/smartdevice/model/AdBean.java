package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.List;

public class AdBean implements Serializable {
    private String spaceId;
    private String name;
    private List<AdCreativeBean> creatives;

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AdCreativeBean> getCreatives() {
        return creatives;
    }

    public void setCreatives(List<AdCreativeBean> creatives) {
        this.creatives = creatives;
    }
}
