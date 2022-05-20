package com.lumos.smartdevice.app;

import java.io.Serializable;

public class LogcatCommandVo implements Serializable {

    private String command;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
