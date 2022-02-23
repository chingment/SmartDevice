package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.HashMap;

public class BookerCustomDataBean implements Serializable {
    private HashMap<String, AdBean> ads;

    public HashMap<String, AdBean> getAds() {
        return ads;
    }

    public void setAds(HashMap<String, AdBean> ads) {
        this.ads = ads;
    }
}
