package com.lumos.smartdevice.api.vo;

import java.io.Serializable;

public class MqttVo implements Serializable {

    private String host;
    private String clientId;
    private String userName;
    private String password;
    private String subTopic;
    private String pubTopic;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public String getPubTopic() {
        return pubTopic;
    }

    public void setPubTopic(String pubTopic) {
        this.pubTopic = pubTopic;
    }
}
