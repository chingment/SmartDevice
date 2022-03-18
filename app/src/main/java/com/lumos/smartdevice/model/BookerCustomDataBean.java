package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class BookerCustomDataBean implements Serializable {
    private HashMap<String, AdBean> ads;
    private List<BookerSlotBean> slots;

    public HashMap<String, AdBean> getAds() {
        return ads;
    }

    public void setAds(HashMap<String, AdBean> ads) {
        this.ads = ads;
    }

    public List<BookerSlotBean> getSlots() {
        return slots;
    }

    public void setSlots(List<BookerSlotBean> slots) {
        this.slots = slots;
    }
}
