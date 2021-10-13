package com.lumos.smartdevice.api;


import com.lumos.smartdevice.db.dao.ConfigDao;
import com.lumos.smartdevice.db.DbManager;
import com.lumos.smartdevice.own.AppVar;

public class ReqInterface {

    private static IReqVersion iReq;

    public static IReqVersion getInstance(){

        if (iReq == null) {
            String version_mode= DbManager.getInstance().getConfigValue(ConfigDao.FIELD_VERSION_MODE);

            switch (version_mode) {
                case "1":
                    iReq = new ReqStandAlone();
                    break;
                case "2":
                    iReq = new ReqNetWord();
                    break;
            }
        }


        return iReq;
    }

    public static IReqVersion getInstance(String version_mode) {

        if (version_mode.equals(AppVar.VERSION_MODE_0))
            return getInstance();

        IReqVersion iReq = null;


        switch (version_mode) {
            case "1":
                iReq = new ReqStandAlone();
                break;
            case "2":
                iReq = new ReqNetWord();
                break;
        }

        return iReq;
    }

}
