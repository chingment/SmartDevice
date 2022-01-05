package com.lumos.smartdevice.model;

import java.io.Serializable;
import java.util.HashMap;

public class DeviceBean implements Serializable {
    private String deviceId;
    private String name;
    private int sceneMode;
    private int versionMode;
    private MerchBean merch;
    private StoreBean store;
    private ShopBean shop;
    private HashMap<String, CabinetBean> cabinets;
    private MqttBean mqtt;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSceneMode() {
        return sceneMode;
    }

    public void setSceneMode(int sceneMode) {
        this.sceneMode = sceneMode;
    }

    public int getVersionMode() {
        return versionMode;
    }

    public void setVersionMode(int versionMode) {
        this.versionMode = versionMode;
    }

    public MerchBean getMerch() {
        return merch;
    }

    public void setMerch(MerchBean merch) {
        this.merch = merch;
    }

    public StoreBean getStore() {
        return store;
    }

    public void setStore(StoreBean store) {
        this.store = store;
    }

    public ShopBean getShop() {
        return shop;
    }

    public void setShop(ShopBean shop) {
        this.shop = shop;
    }

    public HashMap<String, CabinetBean> getCabinets() {
        return cabinets;
    }

    public void setCabinets(HashMap<String, CabinetBean> cabinets) {
        this.cabinets = cabinets;
    }

    public MqttBean getMqtt() {
        return mqtt;
    }

    public void setMqtt(MqttBean mqtt) {
        this.mqtt = mqtt;
    }
}
