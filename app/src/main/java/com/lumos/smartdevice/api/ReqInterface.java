package com.lumos.smartdevice.api;


import com.lumos.smartdevice.db.ConfigDao;
import com.lumos.smartdevice.db.DbManager;

public class ReqInterface {

    private static IReqVersion iReq;

    public static IReqVersion getInstance(){

        if (iReq == null) {
            String version_mode= DbManager.getInstance().getConfigValue(ConfigDao.FIELD_VERSION_MODE);

            switch (version_mode) {
                case "1":
                    iReq = new ReqStandAlone();
                    break;
            }
        }


        return iReq;
    }

}
