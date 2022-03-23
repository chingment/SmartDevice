package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class BookerCustomDataVo implements Serializable {
    private HashMap<String, AdVo> ads;
    private List<BookerSlotVo> slots;

    public HashMap<String, AdVo> getAds() {
        return ads;
    }

    public void setAds(HashMap<String, AdVo> ads) {
        this.ads = ads;
    }

    public List<BookerSlotVo> getSlots() {
        return slots;
    }

    public void setSlots(List<BookerSlotVo> slots) {
        this.slots = slots;
    }
}
