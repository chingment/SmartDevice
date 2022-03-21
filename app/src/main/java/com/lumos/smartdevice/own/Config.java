package com.lumos.smartdevice.own;

import com.lumos.smartdevice.BuildConfig;
import com.lumos.smartdevice.utils.SHA256Encrypt;
import com.lumos.smartdevice.utils.StringUtil;



public class Config {

    public static final boolean IS_BUILD_DEBUG = BuildConfig.DEBUG;//打包模式
    public static final boolean IS_APP_DEBUG = BuildConfig.ISAPPDEBUG;//调试模式

    public static String getSign(String appId, String appKey, String appSecret, String data, String currenttime) {
        // 待加密
        String queryStr =appId+ appKey + appSecret + currenttime + data;
//        LogUtil.e(TAG, "queryStr>>==>>" + queryStr);
        String sortedStr = StringUtil.sortString(queryStr);
//        LogUtil.e(TAG, "sortedStr>>==>>" + sortedStr);
        String sha256edStr = SHA256Encrypt.bin2hex(sortedStr).toLowerCase();
//        LogUtil.e(TAG, "sha256edStr>>==>>" + sha256edStr);
//        String base64Str = Base64.encodeToString(sha256edStr.getBytes(), Base64.NO_WRAP);
//        String base64Str = StringUtils.replaceEnter(Base64.encodeToString(sha256edStr.getBytes(), Base64.NO_WRAP), "");
//        LogUtil.e(TAG, "加密后>>==>>" + base64Str);
        return sha256edStr;
    }

    public class URL {
        public static final String device_InitData = BuildConfig.ENVIRONMENT + "/device/initData";
        public static final String device_Upload = BuildConfig.ENVIRONMENT + "/device/upload";
        public static final String own_LoginByAccount = BuildConfig.ENVIRONMENT + "/own/loginByAccount";
        public static final String own_Logout = BuildConfig.ENVIRONMENT + "/own/logout";
        public static final String own_GetInfo = BuildConfig.ENVIRONMENT + "/own/getInfo";
        public static final String own_SaveInfo = BuildConfig.ENVIRONMENT + "/own/saveInfo";
        public static final String identity_Info = BuildConfig.ENVIRONMENT + "/identity/info";
        public static final String identity_Verify = BuildConfig.ENVIRONMENT + "/identity/verify";
        public static final String booker_CreateFlow = BuildConfig.ENVIRONMENT + "/booker/createFlow";
        public static final String booker_BorrowReturn = BuildConfig.ENVIRONMENT + "/booker/borrowReturn";
        public static final String booker_SawBorrowBooks = BuildConfig.ENVIRONMENT + "/booker/sawBorrowBooks";
        public static final String booker_RenewBooks = BuildConfig.ENVIRONMENT + "/booker/renewBooks";
        public static final String booker_DisplayBooks = BuildConfig.ENVIRONMENT + "/booker/displayBooks";
    }

}
