package com.lumos.smartdevice.api;

import com.lumos.smartdevice.BuildConfig;

public class ReqUrl {
        public static final String device_InitData = BuildConfig.ENVIRONMENT + "/device/initData";
        public static final String device_checkAppVersion = BuildConfig.ENVIRONMENT + "/device/checkAppVersion";
        public static final String device_Upload = BuildConfig.ENVIRONMENT + "/device/upload";
        public static final String own_LoginByAccount = BuildConfig.ENVIRONMENT + "/own/loginByAccount";
        public static final String own_Logout = BuildConfig.ENVIRONMENT + "/own/logout";
        public static final String own_GetInfo = BuildConfig.ENVIRONMENT + "/own/getInfo";
        public static final String own_SaveInfo = BuildConfig.ENVIRONMENT + "/own/saveInfo";
        public static final String identity_Info = BuildConfig.ENVIRONMENT + "/identity/info";
        public static final String identity_Verify = BuildConfig.ENVIRONMENT + "/identity/verify";
        public static final String booker_CreateFlow = BuildConfig.ENVIRONMENT + "/booker/createFlow";
        public static final String booker_BorrowReturn = BuildConfig.ENVIRONMENT + "/booker/borrowReturn";
        public static final String booker_TakeStock = BuildConfig.ENVIRONMENT + "/booker/takeStock";
        public static final String booker_SawBorrowBooks = BuildConfig.ENVIRONMENT + "/booker/sawBorrowBooks";
        public static final String booker_RenewBooks = BuildConfig.ENVIRONMENT + "/booker/renewBooks";
        public static final String booker_DisplayBooks = BuildConfig.ENVIRONMENT + "/booker/displayBooks";
}
