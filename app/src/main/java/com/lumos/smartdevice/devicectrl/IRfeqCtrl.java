package com.lumos.smartdevice.devicectrl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IRfeqCtrl {
    boolean isConnect();
    void setReadHandler(OnReadHandlerListener listener);
    boolean  sendOpenRead(String ant);

    boolean  sendCloseRead();

    Map<String, TagInfo> getRfIds(String ant);

    public  interface OnReadHandlerListener {
        void onData(String epcs);
    }

}
